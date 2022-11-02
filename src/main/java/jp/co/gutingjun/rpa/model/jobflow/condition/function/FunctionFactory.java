package jp.co.gutingjun.rpa.model.jobflow.condition.function;

/**
 * 函数工厂
 *
 * @author sunsx
 * */
public class FunctionFactory {
  public static FunctionModel CreateFunction(String functionStr) {
    FunctionModel newFunc = null;
    if (functionStr.startsWith(FunctionModel.FunctionNameEnum.ISNULL.getFunctionName())) {
      newFunc = new IsNullFunction();
    } else if (functionStr.startsWith(FunctionModel.FunctionNameEnum.GETDATA.getFunctionName())) {
      newFunc = new GetDataFunction();
    } else if (functionStr.startsWith(FunctionModel.FunctionNameEnum.INPUTDATA.getFunctionName())) {
      newFunc = new InputDataFunction();
    } else if (functionStr.startsWith(
        FunctionModel.FunctionNameEnum.OUTPUTDATA.getFunctionName())) {
      newFunc = new OutputDataFunction();
    } else if (functionStr.startsWith(
        FunctionModel.FunctionNameEnum.GETLASTOUTPUTDATA.getFunctionName())) {
      newFunc = new GetLastOutputDataFunction();
    } else if (functionStr.startsWith(
        FunctionModel.FunctionNameEnum.GETLASTEXECUTERESULT.getFunctionName())) {
      newFunc = new GetLastExecuteResultFunction();
    }

    return newFunc;
  }

  public static FunctionModel CreateFunction(FunctionModel.FunctionNameEnum name) {
    return CreateFunction(name.getFunctionName());
  }
}
