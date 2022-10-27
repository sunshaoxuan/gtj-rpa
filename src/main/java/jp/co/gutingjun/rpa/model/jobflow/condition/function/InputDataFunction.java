package jp.co.gutingjun.rpa.model.jobflow.condition.function;

import jp.co.gutingjun.rpa.model.action.IAction;

public class InputDataFunction extends FunctionModel {
  @Override
  public String getFunctionName() {
    return FunctionNameEnum.INPUTDATA.getFunctionName();
  }

  @Override
  public String getPattern() {
    return FunctionNameEnum.INPUTDATA.getFunctionPattern();
  }

  @Override
  public void eval() {
    if (getInputData() instanceof IAction) setResult(((IAction) getInputData()).getInputData());
  }
}
