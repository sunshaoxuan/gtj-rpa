package jp.co.gutingjun.rpa.model.strategy;

import java.io.Serializable;

public interface IBotStrategy  extends Serializable {
    /**
     * 获取策略名称
     *
     * @return
     */
    String getName();

    /**
     * 设置策略名称
     */
    void setName(String value);

    /**
     * 检查有效性
     *
     * @param source 源数据
     * @param  refValue 参考数据
     * @return
     */
    boolean validate(Object source, Object refValue);
}
