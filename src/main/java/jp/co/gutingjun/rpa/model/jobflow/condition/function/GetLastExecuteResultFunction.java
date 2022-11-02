package jp.co.gutingjun.rpa.model.jobflow.condition.function;

import jp.co.gutingjun.rpa.common.RPAConst;

/**
 * 获取上一节点执行结果函数
 *
 * @author sunsx
 */
public class GetLastExecuteResultFunction extends FunctionModel {
  @Override
  public String getFunctionName() {
    return FunctionNameEnum.GETLASTEXECUTERESULT.getFunctionName();
  }

  @Override
  public String getPattern() {
    return FunctionNameEnum.GETLASTEXECUTERESULT.getFunctionPattern();
  }

  @Override
  public void eval() {
    if (getTopContainer().getContext().containsKey(RPAConst.LASTEXECUTERESULT)) {
      setResult(String.valueOf(getTopContainer().getContext().get(RPAConst.LASTEXECUTERESULT)));
    }
  }
}
