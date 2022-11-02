package jp.co.gutingjun.rpa.model.jobflow.condition;

import java.io.Serializable;

/**
 * 条件接口
 *
 * @author sunsx
 */
public interface ICondition extends Serializable {
  Operator getOperator();

  boolean getValue();

  /** 逻辑运算符 */
  enum Operator {
    /** 大于 */
    GT,

    /** 大于等于 */
    GTE,

    /** 小于 */
    LT,

    /** 小于等于 */
    LTE,

    /** 等于 */
    EQ,
    /** 与 */
    AND,

    /** 或 */
    OR,

    /** 非 */
    NOT,

    /** 异或 */
    XOR,

    /** 同或 */
    XNOR
  }
}
