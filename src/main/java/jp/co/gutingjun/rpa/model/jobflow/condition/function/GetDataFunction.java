package jp.co.gutingjun.rpa.model.jobflow.condition.function;

/**
 * 取得数据函数
 *
 * @author sunsx
 */
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
