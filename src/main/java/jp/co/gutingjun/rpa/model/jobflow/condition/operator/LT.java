package jp.co.gutingjun.rpa.model.jobflow.condition.operator;

import jp.co.gutingjun.rpa.model.jobflow.condition.LogicalCondition;

public class LT extends LogicalCondition {
    @Override
    public Operator getOperator() {
        return Operator.LT;
    }

    @Override
    public boolean getValue() {
        if(getLeft() instanceof Number && getRight() instanceof Number){
            return ((Number)getLeft()).doubleValue() < ((Number)getRight()).doubleValue();
        }else if (getLeft() instanceof String && getRight() instanceof String){
            return ((String)getLeft()).compareTo((String)getRight()) < 0;
        }

        throw new RuntimeException("无法对比非数字及字符值的大小");
    }
}