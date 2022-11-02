package jp.co.gutingjun.rpa.model.jobflow.node;

import jp.co.gutingjun.common.pms.TreeNode;
import jp.co.gutingjun.rpa.common.CommonUtils;
import jp.co.gutingjun.rpa.common.NodeTypeEnum;
import jp.co.gutingjun.rpa.common.RPAConst;
import jp.co.gutingjun.rpa.exception.InvalidJobNodeException;
import jp.co.gutingjun.rpa.inf.IContainer;
import jp.co.gutingjun.rpa.model.action.IAction;
import jp.co.gutingjun.rpa.model.bot.BotInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 工作节点模型
 *
 * @author sunsx
 */
@Slf4j
public abstract class JobNodeModel extends TreeNode<LinkNodeModel>
    implements IJobNode, IContainer<BotInstance> {
  private BotInstance parentContainer;
  /** 动作集 */
  private IAction[] actions;
  /** 环境变量集合 */
  private Map<String, Object> context;

  public JobNodeModel() {
    setId(CommonUtils.getNextID());
  }

  @Override
  public IContainer getContainer() {
    return this;
  }

  @Override
  public IContainer getTopContainer() {
    return getParentContainer() != null ? getParentContainer().getTopContainer() : null;
  }

  @Override
  public BotInstance getParentContainer() {
    return parentContainer;
  }

  public void setParentContainer(BotInstance parentContainer) {
    this.parentContainer = parentContainer;
  }

  @Override
  public IAction[] getActions() {
    if (actions == null) {
      actions = new IAction[0];
    }

    return actions;
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
    log.info("    [" + getShowName() + "] 节点开始");
    // 执行本节点所有动作
    Arrays.stream(getActions())
        .forEach(
            action -> {
              log.info("      [" + action.getClass().getSimpleName() + "] 动作开始");
              // 处理节点间传递的输入数据
              dealInputData(action);
              log.info("      [" + action.getClass().getSimpleName() + "] 动作处理节点间传递输入数据");

              // 输出数据采集
              log.info("      [" + action.getClass().getSimpleName() + "] 动作执行");
              Object returnValue = action.execute();

              log.info("      [" + action.getClass().getSimpleName() + "] 动作处理顶层容器环境变量");
              getTopContainer().getContext().put(RPAConst.LASTEXECUTERESULT, returnValue);
              getTopContainer().getContext().put(RPAConst.LASTOUTPUTDATA, action.getOutputData());
              getTopContainer()
                  .getContext()
                  .put(
                      this.getClass().getSimpleName()
                          + "["
                          + this.getTag()
                          + "]."
                          + action.getClass().getSimpleName(),
                      action.getOutputData());
            });
    log.info("    [" + getShowName() + "] 节点结束");

    // 取下级连线节点
    List<LinkNodeModel> linkers = getAllChildren();
    if (linkers != null || linkers.size() > 0) {
      linkers.stream()
          .forEach(
              linker -> {
                linker.execute();
              });
    }
  }

  private void dealInputData(IAction action) {
    if (action.getContext().size() > 0) {
      action
          .getContext()
          .forEach(
              (key, value) -> {
                if (key.equalsIgnoreCase("INPUTDATA")) {
                  action.setInputData(evalFormula((String) value));
                }
              });
    }
  }

  private Object evalFormula(String contextFormula) {
    AtomicReference<Object> rtn = new AtomicReference<>();
    String funcNameTag = "";
    // TODO: 函数识别可改为正则匹配提高输入容错
    // GETOUTPUTDATA
    funcNameTag = "$GETOUTPUTDATA(";
    getSingleRefFunctionValue(contextFormula, rtn, funcNameTag);
    return rtn.get();
  }

  private void getSingleRefFunctionValue(
      String contextFormula, AtomicReference<Object> rtn, String funcNameTag) {
    if (contextFormula.contains(funcNameTag)) {
      String formulaRef = contextFormula.replace(funcNameTag, "").replace(")$", "");
      if (getTopContainer().getContext().size() > 0) {
        getTopContainer()
            .getContext()
            .forEach(
                (key, value) -> {
                  if (((String) key).toUpperCase().startsWith(formulaRef.toUpperCase())) {
                    rtn.set(value);
                  }
                });
      }
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
