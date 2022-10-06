package jp.co.gutingjun.rpa.model.event;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/** 同异步事件管理器 */
@Slf4j
public class EventManager implements Serializable {
  boolean finished = false;

  /** 取完成标志 */
  public boolean isFinished() {
    return finished;
  }

  /** 设置完成标志 */
  public void setFinished(boolean finished) {
    this.finished = finished;
  }

  /** 异步事件处理执行线程 */
  private ExecutorService executorService;

  /** 启动同步事件分发线程 */
  public void startSyncEventDispatchThread() {
    // 初始化事件异步执行线程
    executorService = Executors.newSingleThreadExecutor();
    executorService.execute(new EventWorker());
  }

  /** 停止异步事件分发线程 */
  public void stopSyncEventDispatchThread() {
    // 只允许在未结束时增加一个结束事件
    if (!isFinished()
        && eventQueue.stream()
                .filter(event -> event.getEventType().equals(EventTypeEnum.FINISH))
                .count()
            == 0) {
      eventQueue.add(new BaseEvent(EventTypeEnum.FINISH, this, false));
    }
  }

  /** 停止异步事件分发线程服务 */
  public void stopSyncEventService() {
    executorService.shutdown();
  }

  // 事件类型与事件处理器列表的映射关系
  private final Map<EventTypeEnum, List<IEventHandler<? extends BaseEvent>>> eventListenerMap =
      new HashMap<>();

  // 异步执行的事件队列
  private LinkedBlockingQueue<BaseEvent> eventQueue = new LinkedBlockingQueue<>();

  /**
   * 注册事件
   *
   * @param evtType 事件类型
   * @param listener 具体监听器
   */
  public void registerEvent(EventTypeEnum evtType, IEventHandler<? extends BaseEvent> listener) {
    List<IEventHandler<? extends BaseEvent>> listeners = eventListenerMap.get(evtType);
    if (listeners == null) {
      listeners = new ArrayList<IEventHandler<? extends BaseEvent>>();
      eventListenerMap.put(evtType, listeners);
    }
    listeners.add(listener);
  }

  /**
   * 派发事件
   *
   * @param event
   */
  public void dispatchEvent(BaseEvent event) {
    if (event == null) {
      throw new NullPointerException("事件不能为空");
    }

    if (event.isSync()) {
      // 如果事件是同步的，那么就在消息主线程执行逻辑
      handler(event);
    } else {
      // 否则，就丢到事件线程异步执行
      eventQueue.add(event);
    }
  }

  /**
   * 同步处理器
   *
   * @param event
   */
  private void handler(BaseEvent event) {
    EventTypeEnum evtType = event.getEventType();
    List<IEventHandler<? extends BaseEvent>> listeners = eventListenerMap.get(evtType);
    if (listeners != null) {
      // 一个事件可能被多个事件处理器关注及待处理
      for (IEventHandler listener : listeners) {
        try {
          listener.handleEvent(event);
        } catch (Exception e) {
          // 防止其中一个listener报异常而中断其他逻辑
          log.error(e.getMessage());
        }
      }
    } else {
      log.error("未找到事件处理器");
    }
  }

  /** 异步执行线程 */
  public class EventWorker implements Runnable {
    @Override
    public void run() {
      while (true) {
        try {
          BaseEvent event = eventQueue.take();
          if (event.getEventType() == EventTypeEnum.FINISH) {
            setFinished(true);
            break;
          }
          handler(event);
        } catch (InterruptedException e) {
          log.error(e.getMessage());
        }
      }
    }
  }
}