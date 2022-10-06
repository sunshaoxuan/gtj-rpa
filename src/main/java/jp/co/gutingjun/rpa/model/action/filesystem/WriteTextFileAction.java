package jp.co.gutingjun.rpa.model.action.filesystem;

import jp.co.gutingjun.rpa.model.action.base.ActionModel;
import org.springframework.stereotype.Component;

@Component
public class WriteTextFileAction extends ActionModel {
    @Override
    protected Object beforeDoAction(Object inputData) {
        return super.beforeDoAction(inputData);
    }

    @Override
    protected Object doAction(Object inputData) {
        return null;
    }

    @Override
    protected Object afterDoAction(Object outputData) {
        return super.afterDoAction(outputData);
    }

    @Override
    public void validate(Object inputData) throws Exception {

    }
}
