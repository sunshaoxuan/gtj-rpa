package jp.co.gutingjun.rpa.model.jobflow.condition.function;

import jp.co.gutingjun.rpa.model.action.base.IAction;

public class getOutputDataFunction extends BaseFunction {
  @Override
  public String getFuncionName() {
    return FunctionNameEnum.OUTPUTDATA.getFunctionName();
  }

  @Override
  public String getPattern() {
    return FunctionNameEnum.OUTPUTDATA.getFunctionPattern();
  }

  @Override
  public void eval() {
    if (getInputData() instanceof IAction) {
      setOutputData(((IAction) getInputData()).getOutputData());
    }
  }
}
