package jp.co.gutingjun.rpa.model.event;

/**
 * 事件类型
 *
 * @author sunsx
 * */
public enum EventTypeEnum {
  /** 等待 */
  WAIT,
  /** 准备 */
  PREPARE,
  /** 执行 */
  RUN,
  /** 暂停 */
  PAUSE,
  /** 继续 */
  RESUME,
  /** 停止 */
  STOP,
  /** 完成 */
  FINISH
}
