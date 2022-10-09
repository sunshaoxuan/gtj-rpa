package jp.co.gutingjun.rpa.model.jobflow.node;

import jp.co.gutingjun.common.pms.TreeNode;
import jp.co.gutingjun.rpa.common.CommonUtils;
import jp.co.gutingjun.rpa.common.NodeTypeEnum;

import java.util.HashMap;
import java.util.Map;

public abstract class LinkNodeModel extends TreeNode<JobNodeModel> implements ILinkNode {
  /** 环境变量集合 */
  private Map<String, Object> context;

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
}
