package jp.co.gutingjun.rpa.model.jobflow.condition.function;

public class FunctionFactory {
  public static BaseFunction CreateFunction(String functionStr) {
    BaseFunction newFunc = null;
    if (functionStr.startsWith(BaseFunction.FunctionNameEnum.ISNULL.getFunctionName())) {
      newFunc = new IsNullFunction();
    } else if (functionStr.startsWith(BaseFunction.FunctionNameEnum.GETDATA.getFunctionName())) {
      newFunc = new GetDataFunction();
    } else if (functionStr.startsWith(BaseFunction.FunctionNameEnum.INPUTDATA.getFunctionName())) {
      newFunc = new InputDataFunction();
    } else if (functionStr.startsWith(BaseFunction.FunctionNameEnum.OUTPUTDATA.getFunctionName())) {
      newFunc.setFunctionStr(functionStr);
    }
    return newFunc;
  }

  public static BaseFunction CreateFunction(BaseFunction.FunctionNameEnum name) {
    return CreateFunction(name.getFunctionName());
  }
}
