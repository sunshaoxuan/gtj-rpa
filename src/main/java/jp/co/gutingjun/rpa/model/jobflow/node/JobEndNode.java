package jp.co.gutingjun.rpa.model.jobflow.node;

import jp.co.gutingjun.rpa.common.NodeTypeEnum;

public class JobEndNode extends JobNodeModel {
  public JobEndNode() {
    setTag(null);
    setShowName("结束");
  }

  @Override
  public void execute() {
    super.execute();
  }

  @Override
  public NodeTypeEnum getNoteType() {
    return NodeTypeEnum.END;
  }
}
