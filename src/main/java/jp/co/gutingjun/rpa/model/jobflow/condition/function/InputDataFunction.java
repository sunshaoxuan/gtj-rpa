package jp.co.gutingjun.rpa.model.jobflow.condition.function;

import jp.co.gutingjun.rpa.model.action.base.IAction;

public class InputDataFunction extends BaseFunction {
  @Override
  public String getFuncionName() {
    return FunctionNameEnum.INPUTDATA.getFunctionName();
  }

  @Override
  public String getPattern() {
    return FunctionNameEnum.INPUTDATA.getFunctionPattern();
  }

  @Override
  public void eval() {
    if (getInputData() instanceof IAction) setOutputData(((IAction) getInputData()).getInputData());
  }
}
