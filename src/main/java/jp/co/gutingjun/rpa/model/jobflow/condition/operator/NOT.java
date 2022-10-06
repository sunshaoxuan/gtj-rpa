package jp.co.gutingjun.rpa.model.jobflow.condition.operator;

import jp.co.gutingjun.rpa.model.jobflow.condition.LogicalCondition;

/**
 * 逻辑非运算：无右端判断，值为左端逻辑值的反
 *
 * @author ssx
 * @created 2022-08-16
 */
public class NOT extends LogicalCondition {
  @Override
  public Operator getOperator() {
    return Operator.NOT;
  }

  @Override
  public boolean getValue() {
    return !getLogicalValue(getLeft());
  }
}