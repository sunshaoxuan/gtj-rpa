package jp.co.gutingjun.rpa.model.jobflow.condition.operator;

import jp.co.gutingjun.rpa.model.jobflow.condition.LogicalCondition;

public class EQ extends LogicalCondition {
  @Override
  public Operator getOperator() {
    return Operator.EQ;
  }

  @Override
  public boolean getValue() {
    return getLogicalValue(getLeft()) == getLogicalValue(getRight());
  }
}
