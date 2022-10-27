package jp.co.gutingjun.rpa.model.jobflow.condition.operator;

import jp.co.gutingjun.rpa.model.jobflow.condition.LogicalConditionModel;

public class EQ extends LogicalConditionModel {
  public EQ(String leftCon, String rightCon) {
    setLeft(leftCon);
    setRight(rightCon);
  }

  public EQ() {}

  @Override
  public Operator getOperator() {
    return Operator.EQ;
  }

  @Override
  public boolean getValue() {
    return getLogicalValue(getLeft()) == getLogicalValue(getRight());
  }
}
