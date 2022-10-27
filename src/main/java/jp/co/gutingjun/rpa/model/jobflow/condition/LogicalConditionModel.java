package jp.co.gutingjun.rpa.model.jobflow.condition;

import jp.co.gutingjun.rpa.common.Calculator;
import jp.co.gutingjun.rpa.inf.IContainer;
import jp.co.gutingjun.rpa.model.jobflow.condition.function.FunctionFactory;
import jp.co.gutingjun.rpa.model.jobflow.condition.function.FunctionModel;
import jp.co.gutingjun.rpa.model.jobflow.node.LinkNodeModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class LogicalConditionModel implements ICondition, IContainer<LinkNodeModel> {
  static Stack<String[]> functionStack = new Stack();
  private static Map<String, String> functionDefine = null;
  private Object leftObject;
  private Object rightObject;
  private Object[] objects;

  private LinkNodeModel parentContainer;

  private Map<String, Object> context;

  public static Map<String, String> getFunctionDefine() {
    if (functionDefine == null) {
      functionDefine = new HashMap<>();
      // ISNULL(A,B)
      functionDefine.put(
          FunctionModel.FunctionNameEnum.ISNULL.getFunctionName(),
          FunctionModel.FunctionNameEnum.ISNULL.getFunctionPattern());

      // GETDATE(TABLE,FIELD,WHERE)
      functionDefine.put(
          FunctionModel.FunctionNameEnum.GETDATA.getFunctionName(),
          FunctionModel.FunctionNameEnum.GETDATA.getFunctionPattern());

      // Object.Method().Method()...
      functionDefine.put(
          FunctionModel.FunctionNameEnum.INPUTDATA.getFunctionName(),
          FunctionModel.FunctionNameEnum.INPUTDATA.getFunctionPattern());

      // OUTPUTDATA
      functionDefine.put(
          FunctionModel.FunctionNameEnum.OUTPUTDATA.getFunctionName(),
          FunctionModel.FunctionNameEnum.OUTPUTDATA.getFunctionPattern());

      // GETLASTOUTPUTDATA
      functionDefine.put(
          FunctionModel.FunctionNameEnum.GETLASTOUTPUTDATA.getFunctionName(),
          FunctionModel.FunctionNameEnum.GETLASTOUTPUTDATA.getFunctionPattern());

      // GETLASTEXECUTERESULT
      functionDefine.put(
          FunctionModel.FunctionNameEnum.GETLASTEXECUTERESULT.getFunctionName(),
          FunctionModel.FunctionNameEnum.GETLASTEXECUTERESULT.getFunctionPattern());
    }

    return functionDefine;
  }

  public static LogicalConditionModel getCondition(
      String formulaName, Object formulaObj, Object inputData) {
    LogicalConditionModel condition = ConditionFactory.createCondition(formulaName);
    if (condition != null) {
      Object leftObj = ((Map<String, Object>) formulaObj).get("left");
      Object rightObj = ((Map<String, Object>) formulaObj).get("right");
      condition.setLeft(leftObj);
      condition.setRight(rightObj);
      condition.getContext().put("INPUTDATA", inputData);
    }
    return condition;
  }

  /**
   * 获取表达式逻辑值
   *
   * @param formula 表达式
   * @return
   */
  public boolean getLogicalValue(Object formula) {
    boolean rtn;
    if (formula instanceof ICondition) {
      rtn = ((ICondition) formula).getValue();
    } else {
      rtn = evalLogicalValue((String) formula);
    }
    return rtn;
  }

  /**
   * 获取表达式字符串式
   *
   * @param formula 表达式
   * @return
   */
  public String getStringValue(Object formula) throws Exception {
    String rtn = "";
    if (formula instanceof String) {
      rtn = parseStringValue((String) formula);
    }
    return rtn;
  }

  private String parseStringValue(String formula) throws Exception {
    if (containsFunction(formula)) {
      return evalFunctions(formula);
    } else {
      return formula;
    }
  }

  private String evalFunctions(String formula) {
    int minPos = Integer.MAX_VALUE;
    String minFun = "";

    for (String function : getFunctionDefine().keySet()) {
      if (formula.contains(function)) {
        if (formula.indexOf(function) < minPos) {
          minPos = formula.indexOf(function);
          minFun = function;
          break;
        }
      }
    }

    if (minPos != Integer.MAX_VALUE && !minFun.equals("")) {
      // 有函数需要解析
      Pattern pattern = Pattern.compile(getFunctionDefine().get(minFun));
      Matcher matcher = pattern.matcher(formula);
      Map<String, String> funcValueMap = new HashMap<>();
      if (matcher.find()) {
        for (int i = 0; i <= matcher.groupCount(); i++) {
          String function = matcher.group(i);

          if (!funcValueMap.containsKey(function)) {
            String funcValue = evalFunction(function);
            funcValueMap.put(function, funcValue);
          }
        }
      }

      AtomicReference<String> finalFormula = new AtomicReference<>("");
      funcValueMap.forEach(
          (func, value) -> {
            finalFormula.set(formula.replace(func, value));
          });

      return finalFormula.get();
    }

    return formula;
  }

  private String evalFunction(String function) {
    FunctionModel bf = FunctionFactory.CreateFunction(function);
    bf.setParentContainer(this);
    String[] refs = bf.getRefs();
    for (String ref : refs) {
      function.replace(ref, evalFunctions(ref));
    }
    bf.setFunctionStr(function);
    bf.eval();
    return String.valueOf(bf.getResult());
  }

  private boolean containsFunction(String formula) {
    for (String function : getFunctionDefine().keySet()) {
      if (formula.contains(function)) {
        return true;
      }
    }

    return false;
  }

  private boolean evalLogicalValue(String formula) {
    if (containsFunction(formula)) {
      // 预定义函数
      return evalLogicalValue(evalFunctions(formula));
    } else if (formula.equalsIgnoreCase("true") || formula.equalsIgnoreCase("false")) {
      // 逻辑值
      return Boolean.valueOf(formula);
    } else {
      // 数学表达式
      return Calculator.calculate(formula) != 0;
    }
  }

  public Map<String, Object> getContext() {
    if (context == null) {
      context = new HashMap<String, Object>();
    }

    return context;
  }

  public void setContext(Map<String, Object> context) {
    this.context = context;
  }

  public Object getLeft() {
    return leftObject;
  }

  public void setLeft(Object logicalFormula) {
    if (logicalFormula instanceof String) {
      leftObject = logicalFormula;
    } else if (logicalFormula instanceof Map) {
      Optional<Map.Entry<String, Object>> rootCondition =
          ((Map<String, Object>) logicalFormula).entrySet().stream().findFirst();
      leftObject =
          getCondition(
              rootCondition.get().getKey(),
              rootCondition.get().getValue(),
              getContext().get("INPUTDATA"));
    }
  }

  public Object getRight() {
    return rightObject;
  }

  public void setRight(Object logicalFormula) {
    if (logicalFormula instanceof String) {
      rightObject = logicalFormula;
    } else if (logicalFormula instanceof Map) {
      Optional<Map.Entry<String, Object>> rootCondition =
          ((Map<String, Object>) logicalFormula).entrySet().stream().findFirst();
      rightObject =
          getCondition(
              rootCondition.get().getKey(),
              rootCondition.get().getValue(),
              getContext().get("INPUTDATA"));
    }
  }

  public Object[] getObjects() {
    return objects;
  }

  public void setObjects(Object[] objects) {
    this.objects = objects;
  }

  @Override
  public LinkNodeModel getParentContainer() {
    return parentContainer;
  }

  public void setParentContainer(LinkNodeModel parentContainer) {
    this.parentContainer = parentContainer;
  }

  @Override
  public IContainer getContainer() {
    return this;
  }

  @Override
  public IContainer getTopContainer() {
    return getParentContainer() != null ? getParentContainer().getTopContainer() : null;
  }
}
