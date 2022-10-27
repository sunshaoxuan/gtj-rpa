package jp.co.gutingjun.rpa.model.jobflow.condition.function;

import jp.co.gutingjun.rpa.common.RPAConst;

public class GetLastOutputDataFunction extends FunctionModel {
  @Override
  public String getFunctionName() {
    return FunctionNameEnum.GETLASTOUTPUTDATA.getFunctionName();
  }

  @Override
  public String getPattern() {
    return FunctionNameEnum.GETLASTOUTPUTDATA.getFunctionPattern();
  }

  @Override
  public void eval() {
    if (getTopContainer().getContext().containsKey(RPAConst.LASTOUTPUTDATA)) {
      setResult(String.valueOf(getTopContainer().getContext().get(RPAConst.LASTOUTPUTDATA)));
    }
  }
}
