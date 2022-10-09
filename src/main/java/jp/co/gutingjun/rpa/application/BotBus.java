package jp.co.gutingjun.rpa.application;

import jp.co.gutingjun.common.util.JsonUtils;
import jp.co.gutingjun.rpa.application.bot.SimpleBot;
import jp.co.gutingjun.rpa.config.BotConfig;
import jp.co.gutingjun.rpa.model.bot.BotInstance;
import jp.co.gutingjun.rpa.model.bot.BotModel;
import jp.co.gutingjun.rpa.model.event.BaseEvent;
import jp.co.gutingjun.rpa.model.event.EventTypeEnum;
import jp.co.gutingjun.rpa.model.event.IEventHandler;
import jp.co.gutingjun.rpa.model.jobflow.node.JobNodeModel;
import jp.co.gutingjun.rpa.model.strategy.CycleStrategy;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 机器人总控
 *
 * @author ssx
 * @since 2022/08/20
 */
public class BotBus implements Serializable {
  /** 机器人候选者集合 */
  private static final HashSet<BotModel> candidateSet = new HashSet<>();

  /** 周期执行机器人合格候选者 */
  private static final HashSet<BotModel> cycleQualifiedCandidates = new HashSet<>();

  /** 机器人实例缓存 */
  private static final Queue<BotInstance> botInstanceCache = new LinkedList<>();

  /** 执行中机器人集合 */
  private static final HashSet<BotInstance> executingBot = new HashSet<>();

  /** 机器人运行线程池 */
  private static final ExecutorService executeService =
      Executors.newFixedThreadPool(BotConfig.getExecutionPoolSize());

  /**
   * 获取机器人候选者集合
   *
   * @return
   */
  public static HashSet<BotModel> getCandidateSet() {
    return candidateSet;
  }

  /**
   * 获取周期执行机器人合格候选者
   *
   * @return
   */
  public static HashSet<BotModel> getCycleQualifiedCandidates() {
    return cycleQualifiedCandidates;
  }

  /**
   * 获取执行中机器人集合
   *
   * @return
   */
  public static HashSet<BotInstance> getExecutingBot() {
    return executingBot;
  }

  /**
   * 注册机器人候选者
   *
   * @param botContext 机器人描述Json
   * @return
   * @throws Exception
   */
  public static BotModel botRegister(String botContext) throws Exception {
    BotModel newBot = new SimpleBot(JsonUtils.json2Map(botContext));
    AtomicBoolean added = new AtomicBoolean(false);
    if (getCandidateSet().size() == 0) {
      getCandidateSet().add(newBot);
      added.set(true);
    } else {
      getCandidateSet()
          .forEach(
              candidate -> {
                if (candidate.getClass().equals(newBot.getClass())) {
                  if (!candidate.isSingleton()) {
                    getCandidateSet().add(newBot);
                    added.set(true);
                  }
                }
              });
    }
    return added.get() ? newBot : null;
  }

  /**
   * 获取机器人实例缓存
   *
   * @return
   */
  public static Queue<BotInstance> getBotInstanceCache() {
    return botInstanceCache;
  }

  /** 扫描候选机器人清单，触发周期执行机器人 */
  @Scheduled(cron = "0/1 * * * * ?")
  public static void scanCandidates() {
    if (getCandidateSet().size() > 0) {
      LocalDateTime now = LocalDateTime.now();
      getCandidateSet().stream()
          .filter(botModel -> !getCycleQualifiedCandidates().contains(botModel))
          .forEach(
              botModel -> {
                if (Arrays.stream(botModel.getBotStrategy())
                    .filter(strategy -> strategy instanceof CycleStrategy)
                    .findFirst()
                    .get()
                    .validate(now, now)) {
                  getCycleQualifiedCandidates().add(botModel);
                }
              });
    }

    if (getCycleQualifiedCandidates().size() > 0) {
      getCycleQualifiedCandidates().stream()
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
                  InstantiateBot(botModel);
                }
              });
    }
  }

  /**
   * 实例化机器人，将实例推入执行缓冲
   *
   * @param botModel
   */
  private static void InstantiateBot(BotModel botModel) {
    BotInstance newBot = new BotInstance();
    newBot.buildBot(botModel);
    // 监听机器人执行结束事件，叶子节点执行结束后视为机器人任务结束
    newBot.registerEvent(
        EventTypeEnum.FINISH,
        (IEventHandler<BaseEvent>)
            event -> {
              if (event.getSoureObject() instanceof JobNodeModel) {
                if (((JobNodeModel) event.getSoureObject()).isLeaf()) {
                  // 机器节点结束在叶子结点，该机器人执行结束
                  getExecutingBot().remove(event.getSoureObject());
                }
              }
            });
  }

  /** 扫描机器人执行缓冲，将缓存中的机器人推入执行线程 */
  @Scheduled(cron = "0/1 * * * * ?")
  public static void runBot() {
    while (getBotInstanceCache().size() > 0) {
      BotInstance bot = getBotInstanceCache().poll();
      if (!getExecutingBot().contains(bot)) {
        // 避免重复执行
        getExecuteService().execute(bot);
        getExecutingBot().add(bot);
      }
    }
  }

  /**
   * 获取机器执行线程服务
   *
   * @return
   */
  public static ExecutorService getExecuteService() {
    return executeService;
  }

  /** 停止机器人执行线程服务 */
  public static void shutDown() {
    getExecuteService().shutdown();
  }

  /**
   * 按ID将机器人推入执行线程池
   *
   * @param botId 机器人ID
   */
  public static void manualExecuteBot(Long botId) {
    BotModel foundBot =
        getCandidateSet().stream().filter(botModel -> botModel.getId() == botId).findFirst().get();
    if (foundBot != null) {
      InstantiateBot(foundBot);
    }
  }
}
