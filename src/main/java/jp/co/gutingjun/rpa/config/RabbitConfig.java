package jp.co.gutingjun.rpa.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列服务初始化配置
 *
 * @author sunsx
 */
@Configuration
public class RabbitConfig {
  /** 机器人运行队列 */
  public static final String RPA_RUNNING_CACHE_QUEUE = "RPA_RUNNING_CACHE_QUEUE";

  /**
   * 初始化机器人执行等待队列
   *
   * @return
   */
  @Bean
  public Queue initBotRunningCacheQueue() {
    return new Queue(RPA_RUNNING_CACHE_QUEUE);
  }

}
