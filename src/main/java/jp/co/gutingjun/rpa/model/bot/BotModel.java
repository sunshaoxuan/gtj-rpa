package jp.co.gutingjun.rpa.model.bot;

import jp.co.gutingjun.common.util.ObjectUtils;
import jp.co.gutingjun.rpa.application.action.BaseActionFetcher;
import jp.co.gutingjun.rpa.common.*;
import jp.co.gutingjun.rpa.model.action.base.AbsoluteActionFetcher;
import jp.co.gutingjun.rpa.model.action.base.IAction;
import jp.co.gutingjun.rpa.model.event.BaseEvent;
import jp.co.gutingjun.rpa.model.event.EventManager;
import jp.co.gutingjun.rpa.model.event.EventTypeEnum;
import jp.co.gutingjun.rpa.model.jobflow.node.*;
import jp.co.gutingjun.rpa.model.strategy.CycleStrategy;
import jp.co.gutingjun.rpa.model.strategy.IBotStrategy;
import jp.co.gutingjun.rpa.model.strategy.UnconditionalStrategy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 机器人模型
 *
 * @author ssx
 * @since 2022/08/01
 */
@Slf4j
@Data
public abstract class BotModel extends EventManager implements IBot {
  /** 机器人模型ID */
  private Long id = 0L;
  /** 机器人名称 */
  private String name = "";
  /** 机器人描述 */
  private String description = "";
  /** 创建时间 */
  private Date createdTime = null;
  /** 创建人 */
  private Long createdBy = 0L;
  /** 任务节点 */
  private JobNodeModel jobNode = null;
  /** 单实例运行 */
  private boolean isSingleton = true;
  /** 错误处理方式 */
  private AfterErrorActionEnum onErrorAction;
  /** 已暂停 */
  private boolean paused = false;
  /** 已停止 */
  private boolean stopped = false;

  private IBotStrategy[] botStrategy;
  /** 动作查找器 */
  private AbsoluteActionFetcher actionFetcher;

  /** 初始化机器人模型 */
  public BotModel() {
    setId(CommonUtils.getNextID());
  }

  public AbsoluteActionFetcher getActionFetcher() {
    if (actionFetcher == null) {
      actionFetcher = new BaseActionFetcher();
    }
    return actionFetcher;
  }

  @Override
  public IBotStrategy[] getBotStrategy() {
    if (botStrategy == null) {
      return new IBotStrategy[0];
    }

    return botStrategy;
  }

  /**
   * 用模型设置数据初始化机器人
   *
   * @param botSettings 模型设置数据
   */
  @Override
  public void load(Map<String, Object> botSettings) {
    setBasePropertyBySetting(botSettings);
    setStrategyBySetting(botSettings);
    createJobNodes(botSettings);
  }

  private void setStrategyBySetting(Map<String, Object> botSettings) {
    Map<String, Object> settings = (Map<String, Object>) botSettings.get("bot");
    if (settings.containsKey("strategies") && settings.get("strategies") instanceof List) {
      ((List) settings.get("strategies"))
          .stream()
              .forEach(
                  strategy -> {
                    String sType = (String) ((Map<String, Object>) strategy).get("type");
                    appendStrategyByName(sType, (Map<String, Object>) strategy);
                  });
    }
  }

  private void appendStrategyByName(String strategyName, Map<String, Object> strategy) {
    List<IBotStrategy> newStrategies = null;
    if (getBotStrategy().length == 0) {
      newStrategies = new ArrayList<>();
    } else {
      newStrategies = Arrays.stream(getBotStrategy()).collect(Collectors.toList());
    }

    if (strategyName.equals(UnconditionalStrategy.class.getSimpleName())) {
      newStrategies.add(new UnconditionalStrategy());
    } else if (strategyName.equals(CycleStrategy.class.getSimpleName())) {
      CycleStrategy cycleStrategy = new CycleStrategy();
      strategy.forEach(
          (key, value) -> {
            AtomicReference<Object> finalValue = new AtomicReference<>();
            Arrays.stream(CycleStrategy.class.getDeclaredFields())
                .filter(field -> field.getName().equals(key))
                .forEach(
                    field -> {
                      if (field.getType().isArray()
                          && field.getType().getComponentType().equals(String.class)) {
                        if (StringUtils.isBlank((String) value)) {
                          finalValue.set(
                              Arrays.stream(((String) value).split(","))
                                  .filter(val -> !StringUtils.isBlank(val))
                                  .collect(Collectors.toList())
                                  .toArray(new String[0]));
                        }
                      } else if (field.getType().equals(CycleTypeEnum.class)) {
                        if (!StringUtils.isBlank((String) value)) {
                          finalValue.set(
                              CycleTypeEnum.getCycleTypeEnum(((String) value).toUpperCase()));
                        }
                      } else if (field.getType().equals(FrequencyTypeEnum.class)) {
                        if (!StringUtils.isBlank((String) value)) {
                          finalValue.set(
                              FrequencyTypeEnum.getFrequencyTypeEnum(
                                  ((String) value).toUpperCase()));
                        }
                      } else if (field.getType().equals(LocalDateTime.class)) {
                        if (!StringUtils.isBlank((String) value)) {
                          finalValue.set(LocalDateTime.parse(((String) value).replace(" ", "T")));
                        }
                      } else if (field.getType().equals(LocalTime.class)) {
                        finalValue.set(LocalTime.parse((String) value));
                      } else {
                        finalValue.set(value);
                      }
                    });
            ObjectUtils.setFieldValue(cycleStrategy, key, finalValue.get());
          });
      newStrategies.add(cycleStrategy);
    }

    botStrategy = newStrategies.toArray(new IBotStrategy[0]);
  }

  private void createJobNodes(Map<String, Object> botSettings) {
    // 创建开始节点
    JobNodeModel currentNode;
    List<JobNodeModel> addedJobNodes = new ArrayList<>();
    List<String> fetchedJobNodes = new ArrayList<>();
    Map<String, Object> settings = (Map<String, Object>) botSettings.get("bot");
    if (settings.containsKey("jobs") && settings.containsKey("linkers")) {
      // 有任务节点及连线，递归建树
      // 创建开始节点
      currentNode = new JobBeginNode();
      List<JobNodeModel> addedNodes;
      do {
        // 获取当前节点的后续连线
        fetchLinkers(settings, currentNode);
        // 获取当前节点所有连线的后续工作结点
        addedNodes = fetchLinkedJobNodes(settings, currentNode);
        if (addedNodes.size() > 0) {
          addedJobNodes.addAll(addedNodes);
          fetchedJobNodes.add(currentNode.getTag());
          currentNode = getNextNode(addedJobNodes, fetchedJobNodes);
        } else {
          break;
        }
      } // while ((!isAllNodeFetched(addedJobNodes, fetchedJobNodes)));
      while (addedNodes.size() > 0);

      // 补充结束节点
      addedJobNodes.stream()
          .filter(node -> node.isLeaf())
          .forEach(
              node -> {
                JobEndNode endNode = new JobEndNode();
                BaseLinkNode newTrueLink = new BaseLinkNode();
                newTrueLink.appendChild(endNode);
                newTrueLink.setParent(node);
                newTrueLink.setShowName("无条件结束");
                endNode.setParent(newTrueLink);
                node.appendChild(newTrueLink);
              });

      setJobNode((JobNodeModel) currentNode.getRoot());
    }
  }

  private JobNodeModel getNextNode(List<JobNodeModel> addedJobNodes, List<String> fetchedJobNodes) {
    return addedJobNodes.stream()
        .filter(node -> !fetchedJobNodes.contains(node.getTag()))
        .findFirst()
        .get();
  }

  private boolean isAllNodeFetched(List<JobNodeModel> addedJobNodes, List<String> fetchedJobNodes) {
    return fetchedJobNodes.stream()
            .filter(
                nodeTag ->
                    addedJobNodes.stream()
                            .filter(
                                node ->
                                    (node.getTag() == null && nodeTag == null)
                                        || node.getTag().equals(nodeTag))
                            .count()
                        > 0)
            .count()
        == addedJobNodes.size();
  }

  private List<JobNodeModel> fetchLinkedJobNodes(
      Map<String, Object> botSettings, JobNodeModel currentNode) {
    List<JobNodeModel> addedjobNodes = new ArrayList<>();
    if (currentNode.getAllChildren() != null) {
      // 查找所有连线的下级节点
      currentNode.getAllChildren().stream()
          .forEach(
              linkNode -> {
                ((List<Map<String, Object>>) botSettings.get("jobs"))
                    .stream()
                        .filter(
                            job ->
                                job.containsKey("id")
                                    && job.get("id").equals(linkNode.getNextTag()))
                        .forEach(
                            job -> {
                              JobActionNode newJobNode = new JobActionNode();
                              newJobNode.setShowName((String) job.get("showName"));
                              newJobNode.setParent(linkNode);
                              newJobNode.setTag((String) job.get("id"));
                              AbsoluteActionFetcher actionFetcher = getActionFetcher();
                              IAction newAction = actionFetcher.getAction((String) job.get("type"));
                              Map<String, Object> contextMap =
                                  (Map<String, Object>) job.get("context");
                              if (contextMap != null && contextMap.size() > 0) {
                                contextMap.forEach(
                                    (key, value) -> {
                                      newJobNode.getContext().put(key.toUpperCase(), value);
                                    });
                              }
                              CommonUtils.mapPutAll(
                                  newAction.getContext(), newJobNode.getContext());
                              newJobNode.appendAction(newAction);
                              linkNode.appendChild(newJobNode);
                              addedjobNodes.add(newJobNode);
                            });
              });
    }
    return addedjobNodes;
  }

  private void fetchLinkers(Map<String, Object> botSettings, JobNodeModel currentNode) {
    // 查找与开始节点相连的条件连线
    ((List<Map<String, Object>>) botSettings.get("linkers"))
        .stream()
            .filter(
                linker ->
                    (!linker.containsKey("from") && currentNode.getTag() == null)
                        || (linker.containsKey("from")
                            && linker.get("from").equals(currentNode.getTag())))
            .forEach(
                linker -> {
                  BaseLinkNode newLinkNode = new BaseLinkNode();
                  newLinkNode.setParent(currentNode);
                  newLinkNode.setShowName((String) linker.get("showName"));
                  newLinkNode.setRuleCondition((Map<String, Object>) linker.get("rule"));
                  newLinkNode.setPriorTag((String) linker.get("from"));
                  newLinkNode.setNextTag((String) linker.get("to"));
                  newLinkNode.setTag((String) linker.get("id"));
                  currentNode.appendChild(newLinkNode);
                });
  }

  private void setBasePropertyBySetting(Map<String, Object> botSettings) {
    if (botSettings.containsKey("bot")) {
      ((Map<String, Object>) botSettings.get("bot"))
          .forEach((key, value) -> ObjectUtils.setFieldValue(this, key, value));
    }

    setCreatedTime(new Date());
    setCreatedBy(LoginContext.getUserid());
  }

  @Override
  public AfterErrorActionEnum getOnErroAction() {
    return onErrorAction;
  }

  @Override
  public void setOnErrorAction(AfterErrorActionEnum actionEnum) {
    onErrorAction = actionEnum;
  }

  @Override
  public void start() {
    // 启动异步事件处理线程
    startSyncEventDispatchThread();
    dispatchEvent(new BaseEvent(EventTypeEnum.RUN, this, true));

    // 机器人开始执行
    JobNodeModel currentJobNode = getJobNode();
    if (currentJobNode != null) {
      // 派发节点开始事件
      dispatchEvent(new BaseEvent(EventTypeEnum.RUN, currentJobNode, true));
      currentJobNode.execute();
      dispatchEvent(new BaseEvent(EventTypeEnum.FINISH, currentJobNode, true));
    }
  }

  @Override
  public void pause() {
    // 向主线程发送暂停信号，不影响异步事件处理
    paused = true;

    dispatchEvent(new BaseEvent(EventTypeEnum.PAUSE, this, true));
  }

  @Override
  public void resume() {
    // 向主线程发送继续信号，不影响异步事件处理
    paused = false;

    dispatchEvent(new BaseEvent(EventTypeEnum.RESUME, this, true));
  }

  @Override
  public void stop() {
    // 向主线程发送停止信号
    stopped = true;
    dispatchEvent(new BaseEvent(EventTypeEnum.STOP, this, true));

    // 向异步事件处理器发送停止信号
    stopSyncEventDispatchThread();

    // 一旦结束事件被派发，结束异步服务
    while (!isFinished()) {
      stopSyncEventService();
    }
    dispatchEvent(new BaseEvent(EventTypeEnum.FINISH, this, true));
  }
}
