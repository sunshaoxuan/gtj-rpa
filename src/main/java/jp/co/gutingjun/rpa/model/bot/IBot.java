package jp.co.gutingjun.rpa.model.bot;

import jp.co.gutingjun.rpa.common.AfterErrorActionEnum;
import jp.co.gutingjun.rpa.model.jobflow.node.JobNodeModel;
import jp.co.gutingjun.rpa.model.strategy.IBotStrategy;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public interface IBot extends Serializable {
  /**
   * 获取策略集合
   *
   * @return
   */
  IBotStrategy[] getBotStrategy();

  /**
   * 获取工作任务链
   *
   * @return
   */
  JobNodeModel getJobNode();

  /** 设置工作任务链 */
  void setJobNode(JobNodeModel jobNode);

  /**
   * 获取机器人ID
   *
   * @return
   */
  Long getId();

  /**
   * 设置机器人ID
   *
   * @param value
   */
  void setId(Long value);

  /**
   * 获取机器人名称
   *
   * @return
   */
  String getName();

  /**
   * 设置机器人名称
   *
   * @param value
   */
  void setName(String value);

  /**
   * 获取描述
   *
   * @return
   */
  String getDescription();

  /**
   * 设置描述
   *
   * @param value
   */
  void setDescription(String value);

  /**
   * 获取创建时间
   *
   * @return
   */
  Date getCreatedTime();

  /**
   * 设置创建时间
   *
   * @param value
   */
  void setCreatedTime(Date value);

  /**
   * 获取出错时执行动作
   *
   * @return
   */
  AfterErrorActionEnum getOnErroAction();

  /** 设置出错时执行动作 */
  void setOnErrorAction(AfterErrorActionEnum actionEnum);

  /**
   * 获取创建人ID
   *
   * @return
   */
  Long getCreatedBy();

  /**
   * 设置创建人ID
   *
   * @param value
   */
  void setCreatedBy(Long value);

  /** 解析加载 */
  void build(Map<String, Object> botSettings);

  /** 启动 */
  void start();

  /** 暂停 */
  void pause();

  /** 继续 */
  void resume();

  /** 停止 */
  void stop();
}
