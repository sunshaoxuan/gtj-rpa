package jp.co.gutingjun.rpa.model.jobflow.condition;

import jp.co.gutingjun.rpa.model.jobflow.condition.operator.*;

public class ConditionFactory {
  public static LogicalCondition createCondition(String conditionTag) {
    LogicalCondition rtn = null;

    if (conditionTag.toUpperCase().equals("AND")) {
      rtn = new AND();
    } else if (conditionTag.toUpperCase().equals("OR")) {
      rtn = new OR();
    } else if (conditionTag.toUpperCase().equals("GT")) {
      rtn = new GT();
    } else if (conditionTag.toUpperCase().equals("GTE")) {
      rtn = new GTE();
    } else if (conditionTag.toUpperCase().equals("LT")) {
      rtn = new LT();
    } else if (conditionTag.toUpperCase().equals("LTE")) {
      rtn = new LTE();
    } else if (conditionTag.toUpperCase().equals("NOT")) {
      rtn = new NOT();
    } else if (conditionTag.toUpperCase().equals("XOR")) {
      rtn = new XOR();
    } else if (conditionTag.toUpperCase().equals("XNOR")) {
      rtn = new XNOR();
    }
    
    return rtn;
  }
}