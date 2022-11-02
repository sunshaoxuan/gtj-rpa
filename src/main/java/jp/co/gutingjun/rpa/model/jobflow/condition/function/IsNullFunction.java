package jp.co.gutingjun.rpa.model.jobflow.condition.function;

/**
 * 是否为空函数
 *
 * @author sunsx
 */
public class IsNullFunction extends FunctionModel {
  @Override
  public String getFunctionName() {
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

    setResult(rtn);
  }
}
