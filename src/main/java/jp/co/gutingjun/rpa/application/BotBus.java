package jp.co.gutingjun.rpa.application;

import jp.co.gutingjun.common.util.JsonUtils;
import jp.co.gutingjun.rpa.config.BotConfig;
import jp.co.gutingjun.rpa.config.RabbitConfig;
import jp.co.gutingjun.rpa.model.bot.BotInstance;
import jp.co.gutingjun.rpa.model.bot.BotModel;
import jp.co.gutingjun.rpa.model.event.BaseEvent;
import jp.co.gutingjun.rpa.model.event.EventTypeEnum;
import jp.co.gutingjun.rpa.model.event.IEventHandler;
import jp.co.gutingjun.rpa.model.jobflow.node.JobNodeModel;
import jp.co.gutingjun.rpa.model.log.LogData;
import jp.co.gutingjun.rpa.model.log.Logger;
import jp.co.gutingjun.rpa.model.strategy.CycleStrategy;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 机器人总控
 *
 * @author ssx
 * @since 2022/08/20
 */
@EnableScheduling
@Component
public class BotBus implements Serializable {
  private static BotBus localBus;
  private static AmqpTemplate localAmqpTemplate;

  /** 机器人候选者集合 */
  private final HashSet<BotModel> candidateSet = new HashSet<>();

  /** 周期执行机器人合格候选者 */
  private final HashSet<BotModel> cycleQualifiedCandidates = new HashSet<>();

  /** 执行中机器人集合 */
  private final HashSet<BotInstance> executingBot = new HashSet<>();

  /** 机器人运行线程池 */
  private final ExecutorService executeService =
      Executors.newFixedThreadPool(BotConfig.getExecutionPoolSize());

  private final List<Future<Long>> executeFutures = new ArrayList<Future<Long>>();
  @Autowired BotBus botBus;
  @Autowired AmqpTemplate amqpTemplate;

  public BotBus() {
    checkInit();
  }

  public static BotBus getInstance() {
    return localBus;
  }

  protected static AmqpTemplate getLocalAmqpTemplate() {
    return localAmqpTemplate;
  }

  @PostConstruct
  private void checkInit() {
    if (localBus == null && botBus != null) {
      localBus = botBus;
    }

    if (localAmqpTemplate == null && amqpTemplate != null) {
      localAmqpTemplate = amqpTemplate;
    }
  }

  /** 停止机器人执行线程服务 */
  public void shutDown() {
    getInstance().getExecuteService().shutdown();
  }

  /**
   * 获取机器人候选者集合
   *
   * @return
   */
  public HashSet<BotModel> getCandidateSet() {
    return candidateSet;
  }

  /**
   * 获取周期执行机器人合格候选者
   *
   * @return
   */
  public HashSet<BotModel> getCycleQualifiedCandidates() {
    return cycleQualifiedCandidates;
  }

  /**
   * 获取执行中机器人集合
   *
   * @return
   */
  public HashSet<BotInstance> getExecutingBot() {
    return executingBot;
  }

  /**
   * 注册机器人候选者
   *
   * @param botContext 机器人描述Json
   * @return
   * @throws Exception
   */
  public BotModel botRegister(String botContext) throws Exception {
    BotModel newBot = new BasicBot(JsonUtils.json2Map(botContext));
    AtomicBoolean added = new AtomicBoolean(false);
    if (getInstance().getCandidateSet().size() == 0) {
      getInstance().getCandidateSet().add(newBot);
      added.set(true);
    } else {
      getInstance()
          .getCandidateSet()
          .forEach(
              candidate -> {
                if (candidate.getClass().equals(newBot.getClass())) {
                  if (!candidate.isSingleton()) {
                    getInstance().getCandidateSet().add(newBot);
                    added.set(true);
                  }
                }
              });
    }
    return added.get() ? newBot : null;
  }

  /** 扫描机器人执行缓冲，将缓存中的机器人推入执行线程 */
  @RabbitListener(queues = RabbitConfig.RPA_RUNNING_CACHE_QUEUE)
  public void runBot(Long id) {
    try {
      BotModel botModel =
          getCandidateSet().stream().filter(bot -> bot.getId().equals(id)).findFirst().get();
      BotInstance newBot = new BotInstance();
      newBot.initialize(botModel);
      // 监听机器人执行结束事件，叶子节点执行结束后视为机器人任务结束
      newBot.registerEvent(
          EventTypeEnum.FINISH,
          (IEventHandler<BaseEvent>)
              event -> {
                if (event.getSoureObject() instanceof JobNodeModel) {
                  if (((JobNodeModel) event.getSoureObject()).isLeaf()) {
                    // 机器节点结束在叶子结点，该机器人执行结束
                    getInstance().getExecutingBot().remove(event.getSoureObject());
                  }
                }
              });

      if (!isBotExecuting(newBot) && newBot.isSingleton()) {
        // 单例执行
        executeFutures.add(getInstance().getExecuteService().submit(newBot));
        Logger.log(
            LogData.builder()
                .botId(newBot.getId())
                .botInstanceId(newBot.getInstanceId())
                .actionName(newBot.getName())
                .logTime(LocalDateTime.now())
                .message("线程启动")
                .build());
        getInstance().getExecutingBot().add(newBot);
      }
    } catch (Exception ex) {
      Logger.log(LogData.builder().logTime(LocalDateTime.now()).message(ex.getMessage()).build());
    }
  }

  private boolean isBotExecuting(BotInstance newBot) {
    AtomicBoolean exists = new AtomicBoolean(false);
    getInstance()
        .getExecutingBot()
        .forEach(
            bot -> {
              if (bot.getId() == newBot.getId()) {
                exists.set(true);
                return;
              }
            });

    return exists.get();
  }

  /**
   * 获取机器执行线程服务
   *
   * @return
   */
  public ExecutorService getExecuteService() {
    return executeService;
  }

  /**
   * 按ID将机器人推入执行线程池
   *
   * @param botId 机器人ID
   */
  public void manualExecuteBot(Long botId) {
    BotModel foundBot =
        getInstance().getCandidateSet().stream()
            .filter(botModel -> botModel.getId().equals(botId))
            .findFirst()
            .get();
    if (foundBot != null) {
      getInstance().pushBotToMQ(foundBot);
    }
  }

  /** 扫描候选机器人清单，触发周期执行机器人 */
  @Scheduled(cron = "0/5 * * * * ?")
  public void botScan() {
    if (getInstance().getCandidateSet().size() > 0) {
      LocalDateTime now = LocalDateTime.now();
      getInstance().getCandidateSet().stream()
          .filter(botModel -> !getInstance().getCycleQualifiedCandidates().contains(botModel))
          .forEach(
              botModel -> {
                if (Arrays.stream(botModel.getBotStrategy())
                    .filter(strategy -> strategy instanceof CycleStrategy)
                    .findFirst()
                    .get()
                    .validate(now, now)) {
                  if (!(botModel.isSingleton()
                      && getInstance().getExecutingBot().stream()
                              .filter(runningBot -> runningBot.getId().equals(botModel.getId()))
                              .count()
                          > 0)) {
                    getInstance().getCycleQualifiedCandidates().add(botModel);
                  }
                }
              });
    }

    if (getInstance().getCycleQualifiedCandidates().size() > 0) {
      getInstance().getCycleQualifiedCandidates().stream()
          .forEach(
              botModel -> {
                AtomicBoolean qualified = new AtomicBoolean(false);
                Arrays.stream(botModel.getBotStrategy())
                    .filter(strategy -> !(strategy instanceof CycleStrategy))
                    .forEach(
                        strategy -> {
                          qualified.set(qualified.get() & strategy.validate(null, null));
                        });
                if (qualified.get()) {
                  getInstance().pushBotToMQ(botModel);
                }
              });
    }

    if (getInstance().getExecutingBot().size() > 0) {
      if (executeFutures.size() > 0) {
        for (Future<Long> executeFuture : executeFutures) {
          try {
            Long botId = executeFuture.get();
            BotInstance runningBot =
                getInstance().getExecutingBot().stream()
                    .filter(bot -> bot.getInstanceId().equals(botId))
                    .findFirst()
                    .get();
            Logger.log(
                LogData.builder()
                    .botId(runningBot.getId())
                    .botInstanceId(runningBot.getInstanceId())
                    .actionName(runningBot.getName())
                    .logTime(LocalDateTime.now())
                    .message("单实例线程结束")
                    .build());
            getInstance().getExecutingBot().remove(runningBot);
          } catch (Exception ex) {
            Logger.log(
                LogData.builder().logTime(LocalDateTime.now()).message(ex.getMessage()).build());
          }
        }
      } else {
        getInstance().getExecutingBot().clear();
      }
    }
  }

  /**
   * 实例化机器人，将实例推入执行缓冲
   *
   * @param botModel
   */
  private void pushBotToMQ(BotModel botModel) {
    // 推入执行等待缓冲
    getLocalAmqpTemplate().convertAndSend(RabbitConfig.RPA_RUNNING_CACHE_QUEUE, botModel.getId());
  }
}
