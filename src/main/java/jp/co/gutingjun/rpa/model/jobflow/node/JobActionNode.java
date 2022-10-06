package jp.co.gutingjun.rpa.model.jobflow.node;

import jp.co.gutingjun.rpa.common.NodeTypeEnum;
import jp.co.gutingjun.rpa.model.action.base.IAction;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class JobActionNode extends JobNodeModel {
    @Override
    public void execute() {
        Arrays.stream(getActions()).forEach(IAction::execute);
    }

    @Override
    public NodeTypeEnum getNoteType() {
        return NodeTypeEnum.JOBNODE;
    }
}