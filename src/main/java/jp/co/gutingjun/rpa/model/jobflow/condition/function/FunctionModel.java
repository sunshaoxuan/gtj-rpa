package jp.co.gutingjun.rpa.model.jobflow.condition.function;

import jp.co.gutingjun.rpa.inf.IContainer;
import jp.co.gutingjun.rpa.model.jobflow.condition.LogicalConditionModel;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Map;

public abstract class FunctionModel implements IContainer<LogicalConditionModel>, Serializable {
  private Object inputData;

  private Object result;

  private String functionStr;

  private Map<String, Object> context;

  private LogicalConditionModel parentContainer;

  /**
   * 获取函数名称
   *
   * @return
   */
  public abstract String getFunctionName();

  /**
   * 获取函数正则格式表达式
   *
   * @return
   */
  public abstract String getPattern();

  public Object getInputData() {
    return inputData;
  }

  public void setInputData(Object inputData) {
    this.inputData = inputData;
  }

  public Object getResult() {
    return result;
  }

  public void setResult(Object result) {
    this.result = result;
  }

  public String getFunctionStr() {
    return functionStr;
  }

  public void setFunctionStr(String functionStr) {
    this.functionStr = functionStr;
  }

  protected String removeFunctioName() {
    return getFunctionStr().substring(getFunctionName().length(), getFunctionStr().length() - 1);
  }

  /** 解析函数 */
  public abstract void eval();

  public String[] getRefs() {
    if (StringUtils.isBlank(getFunctionStr())) {
      return new String[0];
    }

    return removeFunctioName().split(",");
  }

  public Map<String, Object> getContext() {
    return context;
  }

  public void setContext(Map<String, Object> context) {
    this.context = context;
  }

  @Override
  public IContainer getContainer() {
    return this;
  }

  @Override
  public IContainer getTopContainer() {
    return getParentContainer() != null ? getParentContainer().getTopContainer() : null;
  }

  @Override
  public LogicalConditionModel getParentContainer() {
    return parentContainer;
  }

  public void setParentContainer(LogicalConditionModel condition) {
    parentContainer = condition;
  }

  public enum FunctionNameEnum {
    ISNULL("$ISNULL$", "\\$ISNULL\\$\\(.*,.*\\)"),
    GETDATA("$GETDATA$", "\\$GETDATA\\$\\(.*,.*,.*\\)"),
    INPUTDATA("$INPUTDATA$", "\\$INPUTDATA\\$[\\.\\w\\(\\)]*"),
    OUTPUTDATA("$OUTPUTDATA$", "\\$OUTPUTDATA\\$[\\.\\w\\(\\)]*"),
    GETLASTOUTPUTDATA("$GETLASTOUTPUTDATA$", "\\$GETLASTOUTPUTDATA\\$"),
    GETLASTEXECUTERESULT("$GETLASTEXECUTERESULT$", "\\$GETLASTEXECUTERESULT\\$");

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
