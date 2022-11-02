package jp.co.gutingjun.rpa.model.jobflow.condition.operator;

import jp.co.gutingjun.rpa.model.jobflow.condition.LogicalConditionModel;

/**
 * 逻辑小于等于运算：左端小于等于右端即为真，反之为假
 *
 * @author sunsx
 */
public class LTE extends LogicalConditionModel {
  @Override
  public Operator getOperator() {
    return Operator.LTE;
  }

  @Override
  public boolean getValue() {
    if (getLeft() instanceof Number && getRight() instanceof Number) {
      return ((Number) getLeft()).doubleValue() <= ((Number) getRight()).doubleValue();
    } else if (getLeft() instanceof String && getRight() instanceof String) {
      return ((String) getLeft()).compareTo((String) getRight()) <= 0;
    }

    throw new RuntimeException("无法对比非数字及字符值的大小");
  }
}
