package jp.co.gutingjun.rpa.model.jobflow.condition.function;

public class GetDataFunction extends FunctionModel {
  @Override
  public String getFunctionName() {
    return FunctionNameEnum.GETDATA.getFunctionName();
  }

  @Override
  public String getPattern() {
    return FunctionNameEnum.GETDATA.getFunctionPattern();
  }

  @Override
  public void eval() {}
}
