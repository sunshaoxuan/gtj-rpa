package jp.co.gutingjun.rpa.model.jobflow.node;

import jp.co.gutingjun.rpa.common.NodeTypeEnum;

/**
 * 开始节点
 *
 * @author sunsx
 */
public class JobBeginNode extends JobNodeModel {
  public JobBeginNode() {
    setTag(null);
    setShowName("开始");
  }

  @Override
  public NodeTypeEnum getNoteType() {
    return NodeTypeEnum.BEGIN;
  }

  @Override
  public void execute() {
    super.execute();
  }
}
