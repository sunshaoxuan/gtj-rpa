package jp.co.gutingjun.rpa.model.jobflow.condition.operator;

import jp.co.gutingjun.rpa.model.jobflow.condition.LogicalConditionModel;

/**
 * 逻辑与运算：左右两端相同即为真，反之为假
 *
 * @author ssx
 * @created 2022-08-16
 */
public class AND extends LogicalConditionModel {
  @Override
  public Operator getOperator() {
    return Operator.AND;
  }

  @Override
  public boolean getValue() {
    if (getObjects() != null && getObjects().length > 0) {
      boolean rtn = true;
      for (Object obj : getObjects()) {
        rtn &= getLogicalValue(obj);
      }
      return rtn;
    } else {
      return getLogicalValue(getLeft()) && getLogicalValue(getRight());
    }
  }
}
