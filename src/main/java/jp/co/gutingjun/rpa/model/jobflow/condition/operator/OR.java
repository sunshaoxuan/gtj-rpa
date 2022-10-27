package jp.co.gutingjun.rpa.model.jobflow.condition.operator;

import jp.co.gutingjun.rpa.model.jobflow.condition.LogicalConditionModel;

/**
 * 逻辑或运算：左右两端逻辑值有一个为真即为真，全为假时值为假
 *
 * @author ssx
 * @created 2022-08-16
 */
public class OR extends LogicalConditionModel {
  @Override
  public Operator getOperator() {
    return Operator.OR;
  }

  @Override
  public boolean getValue() {
    if (getObjects() != null && getObjects().length > 0) {
      boolean rtn = true;
      for (Object obj : getObjects()) {
        rtn |= getLogicalValue(obj);
      }
      return rtn;
    } else {
      return getLogicalValue(getLeft()) || getLogicalValue(getRight());
    }
  }
}
