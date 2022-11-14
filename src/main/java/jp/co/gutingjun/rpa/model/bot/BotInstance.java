package jp.co.gutingjun.rpa.model.bot;

import jp.co.gutingjun.rpa.common.CommonUtils;
import jp.co.gutingjun.rpa.inf.IContainer;
import jp.co.gutingjun.rpa.model.action.ActionModel;
import jp.co.gutingjun.rpa.model.jobflow.condition.LogicalConditionModel;
import jp.co.gutingjun.rpa.model.jobflow.node.JobActionNode;
import jp.co.gutingjun.rpa.model.jobflow.node.JobNodeModel;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 机器人进程
 *
 * @author sunsx
 */
@Slf4j
public class BotInstance extends BotModel implements Callable<Long>, IContainer {
  private Long instanceId = null;
  private Map<String, Object> context;

  /** 初始化机器人实例 */
  public BotInstance() {
    setInstanceId(CommonUtils.getNextID());
  }

  public void setContext() {}

  /**
   * 获取实例ID
   *
   * @return
   */
  public Long getInstanceId() {
    return instanceId;
  }

  /**
   * 设置实例ID
   *
   * @param instanceId
   */
  public void setInstanceId(Long instanceId) {
    this.instanceId = instanceId;
  }

  /**
   * 按机器人模型初始化实例
   *
   * @param botModel
   */
  public void initialize(@NotNull IBot botModel) {
    this.setId(botModel.getId());
    this.setCreatedBy(botModel.getCreatedBy());
    this.setCreatedTime(botModel.getCreatedTime());
    this.setName(botModel.getName());
    this.setDescription(botModel.getDescription());
    this.setBotStrategy(botModel.getBotStrategy());
    this.setJobNode((JobNodeModel) botModel.getJobNode().getRoot());
    setParentContainer(getJobNode(), this);
  }

  private void setParentContainer(@NotNull JobNodeModel jobNode, BotInstance botInc) {
    jobNode.setParentContainer(botInc);
    if (jobNode.getActions() != null && jobNode.getActions().length > 0) {
      Arrays.stream(jobNode.getActions())
          .forEach(
              action -> {
                if (jobNode instanceof JobActionNode && action instanceof ActionModel) {
                  ((ActionModel) action).setParentContainer((JobActionNode) jobNode);
                }
              });
    }

    if (jobNode.getChildrenCount() > 0) {
      jobNode
          .getAllChildren()
          .forEach(
              linker -> {
                linker.setParentContainer(botInc);
                if (linker.getRuleCondition() != null) {
                  if (linker.getRuleCondition() instanceof LogicalConditionModel) {
                    ((LogicalConditionModel) linker.getRuleCondition()).setParentContainer(linker);
                  }
                }
                JobNodeModel currentNode = linker.getNextNode();
                if (currentNode != null) {
                  setParentContainer(currentNode, botInc);
                }
              });
    }
  }

  /** 运行机器人 */
  @Override
  public Long call() {
    try {
      super.start();
    } finally {
      return this.getInstanceId();
    }
  }

  @Override
  public IContainer getContainer() {
    return this;
  }

  @Override
  public IContainer getTopContainer() {
    return this;
  }

  @Override
  public IContainer getParentContainer() {
    return null;
  }

  @Override
  public Map<String, Object> getContext() {
    if (context == null) {
      context = new HashMap<>();
    }

    return context;
  }
}
