package jp.co.gutingjun.rpa.model.jobflow.node;

import jp.co.gutingjun.rpa.model.action.IAction;

/**
 * 动作节点接口
 *
 * @author sunsx
 */
public interface IJobNode extends INode {
  /**
   * 获取节点动作集
   *
   * @return
   */
  IAction[] getActions();

  void setActions(IAction[] actions);
}
