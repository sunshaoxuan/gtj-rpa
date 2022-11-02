package jp.co.gutingjun.rpa.application;

import jp.co.gutingjun.rpa.model.bot.BotModel;

import java.util.Map;

/**
 * 基础构建机器人
 *
 * @author sunsx
 */
public class BasicBot extends BotModel {
  public BasicBot(Map<String, Object> botSettings) {
    build(botSettings);
  }
}
