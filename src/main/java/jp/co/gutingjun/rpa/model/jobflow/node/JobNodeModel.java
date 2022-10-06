package jp.co.gutingjun.rpa.model.jobflow.node;

import jp.co.gutingjun.common.pms.TreeNode;
import jp.co.gutingjun.rpa.common.CommonUtils;
import jp.co.gutingjun.rpa.common.NodeTypeEnum;
import jp.co.gutingjun.rpa.exception.InvalidJobNodeException;
import jp.co.gutingjun.rpa.model.action.base.IAction;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class JobNodeModel extends TreeNode<LinkNodeModel> implements IJobNode {
  /** 工作ID */
  private final Long id;

  /** 显示名称 */
  private String showName;

  /** 当前节点标签 */
  private String tag;

  /** 动作集 */
  private IAction[] actions;

  /** 环境变量集合 */
  private Map<String, Object> context;

  public JobNodeModel() {
    id = CommonUtils.getNextID();
  }

  @Override
  public Long getId() {
    return id;
  }

  /**
   * 获取工作节点显示名称
   *
   * @return
   */
  public String getShowName() {
    return showName;
  }

  /**
   * 设置工作节点显示名称
   *
   * @param showName
   */
  public void setShowName(String showName) {
    this.showName = showName;
  }

  @Override
  public String getTag() {
    return tag;
  }

  @Override
  public void setTag(String tag) {
    this.tag = tag;
  }

  @Override
  public IAction[] getActions() {
    return new IAction[0];
  }

  @Override
  public void setActions(IAction[] actions) {
    this.actions = actions;
  }

  /**
   * 增加动作
   *
   * @param action
   */
  public void appendAction(IAction action) {
    List<IAction> actionList = Arrays.stream(getActions()).collect(Collectors.toList());
    actionList.add(action);
    setActions(actionList.toArray(new IAction[0]));
  }

  @Override
  public Map<String, Object> getContext() {
    if (context == null) {
      context = new HashMap<>();
    }
    return context;
  }

  @Override
  public void setContext(Map<String, Object> context) {
    this.context = context;
  }

  /** 执行工作 */
  public void execute() {
    Map<String, Object> outputData = new HashMap<>();
    Map<String, Object> actionContext = new HashMap<>();
    // 执行本节点所有动作
    Arrays.stream(getActions())
        .forEach(
            action -> {
              // 将节点环境变量传递给动作使用
              CommonUtils.mapPutAll(action.getContext(), getContext());

              // 将上一动作环境变量传递给下一动作使用
              if (actionContext.size() > 0) {
                action.getContext().putAll(actionContext);
              }

              // 输出数据采集
              outputData.put(action.getClass().getName() + "[" + getId() + "]", action.execute());

              // 缓存本动作执行后的环境变更
              actionContext.putAll(action.getContext());
            });

    Map<String, Object> lastStepOutputData = new HashMap<>();
    // 用本节点所有动作输出数据构造“上一节点输出数据”
    lastStepOutputData.put(LASTOUTPUTDATA, outputData);
    // 取下级连线节点
    List<LinkNodeModel> linkers = getAllChildren();
    if (linkers != null || linkers.size() > 0) {
      linkers.stream()
          .forEach(
              linker -> {
                // 执行连线评估
                if (linker.eval()) {
                  // 将“上一节点输出数据”传递给下一节点环境变量
                  linker.getNextNode().setContext(lastStepOutputData);
                  // 将当前节点环境变更传递给下一节点
                  CommonUtils.mapPutAll(linker.getNextNode().getContext(), getContext());
                  // 执行评估结果为真的下级工作节点
                  linker.getNextNode().execute();
                }
              });
    }
  }

  @Override
  public void validate() throws Exception {
    // 开始节点之前不能再有节点
    if (getNoteType().equals(NodeTypeEnum.BEGIN)) {
      if (getParent() != null) {
        throw new InvalidJobNodeException();
      }
    }

    // 结束结点之后不能再有节点
    if (getNoteType().equals(NodeTypeEnum.END)) {
      if (getAllChildren() != null) {
        throw new InvalidJobNodeException();
      }
    }
  }
}
