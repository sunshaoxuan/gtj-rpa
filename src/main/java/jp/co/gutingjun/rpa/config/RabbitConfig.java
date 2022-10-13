package jp.co.gutingjun.rpa.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
