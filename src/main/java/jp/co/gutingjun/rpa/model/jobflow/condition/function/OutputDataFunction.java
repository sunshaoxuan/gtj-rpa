package jp.co.gutingjun.rpa.model.jobflow.condition.function;

import jp.co.gutingjun.rpa.model.action.IAction;

/**
 * 输出数据函数
 *
 * @author sunsx
 */
public class OutputDataFunction extends FunctionModel {
  @Override
  public String getFunctionName() {
    return FunctionNameEnum.OUTPUTDATA.getFunctionName();
  }

  @Override
  public String getPattern() {
    return FunctionNameEnum.OUTPUTDATA.getFunctionPattern();
  }

  @Override
  public void eval() {
    if (getInputData() instanceof IAction) {
      setResult(((IAction) getInputData()).getOutputData());
    }
  }
}
