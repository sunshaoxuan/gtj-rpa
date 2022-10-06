package jp.co.gutingjun.rpa.model.jobflow.condition.function;

public class IsNullFunction extends BaseFunction {
  @Override
  public String getFuncionName() {
    return FunctionNameEnum.ISNULL.getFunctionName();
  }

  @Override
  public String getPattern() {
    return FunctionNameEnum.ISNULL.getFunctionPattern();
  }

  @Override
  public void eval() {
    String[] refs = getRefs();

    if (refs.length < 2) {
      throw new RuntimeException("ISNULL参数个数错误");
    }

    Object rtn = null;

    if (refs[0] != null && !refs[0].equalsIgnoreCase("NULL")) {
      rtn = refs[0];
    } else {
      rtn = refs[1];
    }

    setOutputData(rtn);
  }
}