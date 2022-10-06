package jp.co.gutingjun.rpa.model.event;

public class BaseEvent {
  private final EventTypeEnum eventType;

  private final Object soureObject;

  private boolean sync = true;

  /**
   * 按照事件类型声明事件
   *
   * @param eventType 事件类型
   */
  public BaseEvent(EventTypeEnum eventType, Object soureObject) {
    this.eventType = eventType;
    this.soureObject = soureObject;
  }

  /**
   * 按照事件类型及线程处理方式声明事件
   *
   * @param eventType 事件类型
   * @param soureObject 事件触发源
   * @param sync 是否与主线程同步处理
   */
  public BaseEvent(EventTypeEnum eventType, Object soureObject, boolean sync) {
    this.eventType = eventType;
    this.soureObject = soureObject;
    this.sync = sync;
  }

  /**
   * 获取事件类型
   *
   * @return
   */
  public EventTypeEnum getEventType() {
    return eventType;
  }

  /**
   * 是否在消息主线程同步执行
   *
   * @return
   */
  public boolean isSync() {
    return sync;
  }

  /**
   * 设置主线程同步执行方式
   *
   * @param sync
   */
  public void setSync(boolean sync) {
    this.sync = sync;
  }

  /**
   * 获取事件触发源
   *
   * @return
   */
  public Object getSoureObject() {
    return soureObject;
  }
}