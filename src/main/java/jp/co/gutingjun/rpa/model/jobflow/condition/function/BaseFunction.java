package jp.co.gutingjun.rpa.model.jobflow.condition.function;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Map;

public abstract class BaseFunction implements Serializable {
  private Object inputData;
  private Object outputData;
  private String functionStr;
  private Map<String, Object> functionContext;

  public abstract String getFuncionName();

  public abstract String getPattern();

  public Object getInputData() {
    return inputData;
  }

  public void setInputData(Object inputData) {
    this.inputData = inputData;
  }

  public Object getOutputData() {
    return outputData;
  }

  public void setOutputData(Object outputData) {
    this.outputData = outputData;
  }

  public Map<String, Object> getFunctionContext() {
    return functionContext;
  }

  public void setFunctionContext(Map<String, Object> functionContext) {
    this.functionContext = functionContext;
  }

  public String getFunctionStr() {
    return functionStr;
  }

  public void setFunctionStr(String functionStr) {
    this.functionStr = functionStr;
  }

  protected String removeFunctioName() {
    return getFunctionStr().substring(getFuncionName().length(), getFunctionStr().length() - 1);
  }

  public abstract void eval();

  public String[] getRefs() {
    if (StringUtils.isBlank(getFunctionStr())) {
      return new String[0];
    }

    return removeFunctioName().split(",");
  }

  public enum FunctionNameEnum {
    ISNULL("$ISNULL$", "\\$ISNULL\\$\\(.*,.*\\)"),
    GETDATA("$GETDATA$", "\\$GETDATA\\$\\(.*,.*,.*\\)"),
    INPUTDATA("$INPUTDATA$", "\\$INPUTDATA\\$[\\.\\w\\(\\)]*"),
    OUTPUTDATA("$OUTPUTDATA$", "\\$OUTPUTDATA\\$[\\.\\w\\(\\)]*");

    private String functionName = "";
    private String functionPattern = "";

    FunctionNameEnum(String name, String pattern) {
      functionName = name;
      functionPattern = pattern;
    }

    public String getFunctionName() {
      return functionName;
    }

    public String getFunctionPattern() {
      return functionPattern;
    }
  }
}
