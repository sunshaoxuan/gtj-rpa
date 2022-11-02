package jp.co.gutingjun.rpa.model.jobflow.condition.operator;

import jp.co.gutingjun.rpa.model.jobflow.condition.LogicalConditionModel;

/**
 * 逻辑等于运算：左右两端相等即为真，反之为假
 *
 * @author sunsx
 */
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
