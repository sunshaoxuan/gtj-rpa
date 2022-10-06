package jp.co.gutingjun.rpa.model.jobflow.condition.operator;

import jp.co.gutingjun.rpa.model.jobflow.condition.LogicalCondition;

/**
 * 异或逻辑运算：左右两端逻辑值不同即为真，反之为假
 *
 * @author ssx
 * @created 2022-08-16
 */
public class XOR extends LogicalCondition {
  @Override
  public Operator getOperator() {
    return Operator.XOR;
  }

  @Override
  public boolean getValue() {
    return getLogicalValue(getLeft()) ^ getLogicalValue(getRight());
  }
}