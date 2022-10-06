package jp.co.gutingjun.rpa.model.jobflow.condition.operator;

import jp.co.gutingjun.rpa.model.jobflow.condition.LogicalCondition;

/**
 * 同或逻辑运算：左右两端逻辑值相同即为真，反之为假
 *
 * @author ssx
 * @created 2022-08-16
 */
public class XNOR extends LogicalCondition {
  @Override
  public Operator getOperator() {
    return Operator.XNOR;
  }

  @Override
  public boolean getValue() {
    return (getLogicalValue(getLeft()) ^ getLogicalValue(getRight())) ^ true;
  }
}