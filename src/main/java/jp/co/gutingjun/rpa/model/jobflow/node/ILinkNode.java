package jp.co.gutingjun.rpa.model.jobflow.node;

import jp.co.gutingjun.rpa.model.jobflow.condition.ICondition;

/**
 * 连线节点接口
 *
 * @author sunsx
 */
public interface ILinkNode extends INode {
  /**
   * 获取判断规则
   *
   * @return
   */
  ICondition getRuleCondition();

  /**
   * 判断转向条件
   *
   * @return
   */
  boolean eval();
}
