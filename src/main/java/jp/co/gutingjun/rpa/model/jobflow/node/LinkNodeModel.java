package jp.co.gutingjun.rpa.model.jobflow.node;

import jp.co.gutingjun.common.pms.TreeNode;
import jp.co.gutingjun.rpa.common.CommonUtils;
import jp.co.gutingjun.rpa.common.NodeTypeEnum;

import java.util.HashMap;
import java.util.Map;

public abstract class LinkNodeModel extends TreeNode<JobNodeModel> implements ILinkNode {
  /** 工作ID */
  private Long id;

  /** 环境变量集合 */
  private Map<String, Object> context;

  /** 上级节点标签 */
  private String PriorTag;

  /** 下级节点标签 */
  private String nextTag;

  /** 当前节点标签 */
  private String tag;

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
  public String getTag() {
    return tag;
  }

  @Override
  public void setTag(String tag) {
    this.tag = tag;
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

  @Override
  public Long getId() {
    return id;
  }

  /**
   * 取连线目标节点
   *
   * @return
   */
  public JobNodeModel getNextNode() {
    return getChildAt(0);
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

  public LinkNodeModel() {
    id = CommonUtils.getNextID();
  }

  @Override
  public void validate() throws Exception {}
}