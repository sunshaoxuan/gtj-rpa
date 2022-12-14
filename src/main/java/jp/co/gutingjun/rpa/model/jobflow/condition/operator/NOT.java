package jp.co.gutingjun.rpa.model.jobflow.condition.operator;

import jp.co.gutingjun.rpa.model.jobflow.condition.LogicalConditionModel;

/**
 * 逻辑非运算：无右端判断，值为左端逻辑值的反
 *
 * @author sunsx
 */
public class NOT extends LogicalConditionModel {
  @Override
  public Operator getOperator() {
    return Operator.NOT;
  }

  @Override
  public boolean getValue() {
    return !getLogicalValue(getLeft());
  }
}
