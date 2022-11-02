package jp.co.gutingjun.rpa.model.jobflow.node;

import jp.co.gutingjun.rpa.common.NodeTypeEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * 动作节点
 *
 * @author sunsx
 */
@Slf4j
public class JobActionNode extends JobNodeModel {
  @Override
  public void execute() {
    super.execute();
  }

  @Override
  public NodeTypeEnum getNoteType() {
    return NodeTypeEnum.JOBNODE;
  }
}
