package jp.co.gutingjun.rpa.model.event;

import java.io.Serializable;

/**
 * 事件监听接口
 *
 * @param <E>
 */
public interface IEventHandler<E>  extends Serializable {
    /**
     * 事件触发
     * @param event
     */
    public void handleEvent(E event);
}
