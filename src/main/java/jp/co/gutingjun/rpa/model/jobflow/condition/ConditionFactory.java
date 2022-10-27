package jp.co.gutingjun.rpa.model.jobflow.condition;

import jp.co.gutingjun.rpa.model.jobflow.condition.operator.*;

public class ConditionFactory {
  public static LogicalConditionModel createCondition(String conditionTag) {
    LogicalConditionModel rtn = null;

    if (conditionTag.equalsIgnoreCase("AND")) {
      rtn = new AND();
    } else if (conditionTag.equalsIgnoreCase("EQ")) {
      rtn = new EQ();
    } else if (conditionTag.equalsIgnoreCase("OR")) {
      rtn = new OR();
    } else if (conditionTag.equalsIgnoreCase("GT")) {
      rtn = new GT();
    } else if (conditionTag.equalsIgnoreCase("GTE")) {
      rtn = new GTE();
    } else if (conditionTag.equalsIgnoreCase("LT")) {
      rtn = new LT();
    } else if (conditionTag.equalsIgnoreCase("LTE")) {
      rtn = new LTE();
    } else if (conditionTag.equalsIgnoreCase("NOT")) {
      rtn = new NOT();
    } else if (conditionTag.equalsIgnoreCase("XOR")) {
      rtn = new XOR();
    } else if (conditionTag.equalsIgnoreCase("XNOR")) {
      rtn = new XNOR();
    }

    return rtn;
  }
}
