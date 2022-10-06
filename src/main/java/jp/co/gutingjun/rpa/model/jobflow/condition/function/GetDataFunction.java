package jp.co.gutingjun.rpa.model.jobflow.condition.function;

public class GetDataFunction extends BaseFunction{
    @Override
    public String getFuncionName() {
    return FunctionNameEnum.GETDATA.getFunctionName();
    }

    @Override
    public String getPattern() {
        return FunctionNameEnum.GETDATA.getFunctionPattern();
    }

    @Override
    public void eval() {

    }
}
