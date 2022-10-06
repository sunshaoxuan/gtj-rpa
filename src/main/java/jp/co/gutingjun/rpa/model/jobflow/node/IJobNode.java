package jp.co.gutingjun.rpa.model.jobflow.node;

import jp.co.gutingjun.rpa.model.action.base.IAction;

public interface IJobNode extends INode {
    /**
     * 上一节点输出数据
     */
    public static String LASTOUTPUTDATA = "LastOutputData";

    /**
     * 获取节点动作集
     *
     * @return
     */
    IAction[] getActions();

    void setActions(IAction[] actions);
}
