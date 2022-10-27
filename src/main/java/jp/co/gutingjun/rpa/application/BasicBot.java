package jp.co.gutingjun.rpa.application;

import jp.co.gutingjun.rpa.model.bot.BotModel;

import java.util.Map;

public class BasicBot extends BotModel {
  public BasicBot(Map<String, Object> botSettings) {
    build(botSettings);
  }
}
