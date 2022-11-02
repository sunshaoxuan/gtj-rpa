package jp.co.gutingjun.rpa.model.jobflow.condition.operator;

import jp.co.gutingjun.rpa.model.jobflow.condition.LogicalConditionModel;

/**
 * 同或逻辑运算：左右两端逻辑值相同即为真，反之为假
 *
 * @author sunsx
 */
public class XNOR extends LogicalConditionModel {
  @Override
  public Operator getOperator() {
    return Operator.XNOR;
  }

  @Override
  public boolean getValue() {
    return (getLogicalValue(getLeft()) ^ getLogicalValue(getRight())) ^ true;
  }
}
