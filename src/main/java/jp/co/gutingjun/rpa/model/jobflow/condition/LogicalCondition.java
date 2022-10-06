package jp.co.gutingjun.rpa.model.jobflow.condition;

import jp.co.gutingjun.rpa.common.Calculator;
import jp.co.gutingjun.rpa.model.jobflow.condition.function.BaseFunction;
import jp.co.gutingjun.rpa.model.jobflow.condition.function.FunctionFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class LogicalCondition implements ICondition {
  static Stack<String[]> functionStack = new Stack();
  private static Map<String, String> functionDefine = null;
  private Object leftObject;
  private Object rightObject;
  private Object[] objects;

  private Map<String, Object> context;

  public static Map<String, String> getFunctionDefine() {
    if (functionDefine == null) {
      functionDefine = new HashMap<>();
      // ISNULL(A,B)
      functionDefine.put(
          BaseFunction.FunctionNameEnum.ISNULL.getFunctionName(),
          BaseFunction.FunctionNameEnum.ISNULL.getFunctionPattern());

      // GETDATE(TABLE,FIELD,WHERE)
      functionDefine.put(
          BaseFunction.FunctionNameEnum.GETDATA.getFunctionName(),
          BaseFunction.FunctionNameEnum.GETDATA.getFunctionPattern());

      // Object.Method().Method()...
      functionDefine.put(
          BaseFunction.FunctionNameEnum.INPUTDATA.getFunctionName(),
          BaseFunction.FunctionNameEnum.INPUTDATA.getFunctionPattern());

      // OUTPUTDATA
      functionDefine.put(
          BaseFunction.FunctionNameEnum.OUTPUTDATA.getFunctionName(),
          BaseFunction.FunctionNameEnum.OUTPUTDATA.getFunctionPattern());
    }

    return functionDefine;
  }

  public static LogicalCondition getCondition(
      String formulaName, Object formulaObj, Object inputData) {
    LogicalCondition condition = ConditionFactory.createCondition(formulaName);
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
  public static boolean getLogicalValue(Object formula) {
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
  public static String getStringValue(Object formula) throws Exception {
    String rtn = "";
    if (formula instanceof String) {
      rtn = parseStringValue((String) formula);
    }
    return rtn;
  }

  private static String parseStringValue(String formula) throws Exception {
    if (containsFunction(formula)) {
      return evalFunctions(formula);
    } else {
      return formula;
    }
  }

  private static String evalFunctions(String formula) {
    int minPos = Integer.MAX_VALUE;
    String minFun = "";
    do {
      for (String function : getFunctionDefine().keySet()) {
        if (formula.contains(function)) {
          if (formula.indexOf(function) < minPos) {
            minPos = formula.indexOf(function);
            minFun = function;
          }
        }
      }
    } while (containsFunction(formula));

    if (minPos != Integer.MAX_VALUE && !minFun.equals("")) {
      // 有函数需要解析
      Pattern pattern = Pattern.compile(getFunctionDefine().get(minFun));
      Matcher matcher = pattern.matcher(formula);
      Map<String, String> funcValueMap = new HashMap<>();
      if (matcher.find()) {
        for (int i = 0; i < matcher.groupCount(); i++) {
          String function = matcher.group(i);

          if (!funcValueMap.containsKey(function)) {
            String funcValue = evalFunction(function);
            funcValueMap.put(function, funcValue);
          }
        }
      }

      funcValueMap.forEach((func, value) -> formula.replace(func, value));

      return String.valueOf(Calculator.calculate(formula));
    } else {
      // 无函数需要解析，直接返回表达式计算结果
      return String.valueOf(Calculator.calculate(formula));
    }
  }

  private static String evalFunction(String function) {
    BaseFunction bf = FunctionFactory.CreateFunction(function);
    String[] refs = bf.getRefs();
    for (String ref : refs) {
      function.replace(ref, evalFunctions(ref));
    }
    bf.setFunctionStr(function);
    bf.eval();
    return String.valueOf(bf.getOutputData());
  }

  private static boolean containsFunction(String formula) {
    for (String function : functionDefine.keySet()) {
      if (formula.contains(function)) {
        return true;
      }
    }

    return false;
  }

  private static boolean evalLogicalValue(String formula) {
    if (containsFunction(formula)) {
      return evalLogicalValue(evalFunctions(formula));
    } else {
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
}
