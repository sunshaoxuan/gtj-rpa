package jp.co.gutingjun.rpa.model.jobflow.node;

import jp.co.gutingjun.common.pms.TreeNode;
import jp.co.gutingjun.rpa.common.CommonUtils;
import jp.co.gutingjun.rpa.common.NodeTypeEnum;
import jp.co.gutingjun.rpa.inf.IContainer;
import jp.co.gutingjun.rpa.model.bot.BotInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 连线节点模型
 *
 * @author sunsx
 */
@Slf4j
public abstract class LinkNodeModel extends TreeNode<JobNodeModel>
    implements ILinkNode, IContainer<BotInstance> {
  /** 环境变量集合 */
  private Map<String, Object> context;

  private BotInstance parentContainer;
  /** 上级节点标签 */
  private String PriorTag;

  /** 下级节点标签 */
  private String nextTag;

  public LinkNodeModel() {
    setId(CommonUtils.getNextID());
  }

  public String getPriorTag() {
    return PriorTag;
  }

  public void setPriorTag(String priorTag) {
    PriorTag = priorTag;
  }

  public String getNextTag() {
    return nextTag;
  }

  public void setNextTag(String nextTag) {
    this.nextTag = nextTag;
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

  /**
   * 取连线来源节点
   *
   * @return
   */
  public JobNodeModel getPriorNode() {
    return getParent();
  }

  @Override
  public final NodeTypeEnum getNoteType() {
    return NodeTypeEnum.LINKNODE;
  }

  @Override
  public void validate() throws Exception {}

  @Override
  public IContainer getContainer() {
    return this;
  }

  @Override
  public BotInstance getParentContainer() {
    return parentContainer;
  }

  public void setParentContainer(BotInstance parentContainer) {
    this.parentContainer = parentContainer;
  }

  @Override
  public IContainer getTopContainer() {
    return getParentContainer() != null ? getParentContainer().getTopContainer() : null;
  }

  public void execute() {
    log.info("    [" + getShowName() + "] 连线节点执行");
    // 执行连线评估
    if (eval()) {
      // 执行评估结果为真的下级工作节点
      getNextNode().execute();
    }
  }
}
