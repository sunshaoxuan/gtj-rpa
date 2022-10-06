package jp.co.gutingjun.rpa.model.bot;

import jp.co.gutingjun.rpa.common.CommonUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BotInstance extends BotModel implements Runnable {
  private Long instanceId = null;

  /**
   * 获取实例ID
   *
   * @return
   */
  public Long getInstanceId() {
    return instanceId;
  }

  /**
   * 设置实例ID
   *
   * @param instanceId
   */
  public void setInstanceId(Long instanceId) {
    this.instanceId = instanceId;
  }

  /** 初始化机器人实例 */
  public BotInstance() {
    setInstanceId(CommonUtils.getNextID());
  }

  /**
   * 按机器人模型初始化实例
   *
   * @param botModel
   */
  public void buildBot(IBot botModel) {
    this.setId(botModel.getId());
    this.setCreatedBy(botModel.getCreatedBy());
    this.setCreatedTime(botModel.getCreatedTime());
    this.setName(botModel.getName());
    this.setDescription(botModel.getDescription());
    this.setJobNode(botModel.getJobNode());
  }

  /** 运行机器人 */
  @Override
  public void run() {
    super.start();
  }
}