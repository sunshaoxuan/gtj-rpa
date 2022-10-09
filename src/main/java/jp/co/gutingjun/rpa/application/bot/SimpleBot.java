package jp.co.gutingjun.rpa.application.bot;

import jp.co.gutingjun.rpa.model.bot.BotModel;

import java.util.Map;

public class SimpleBot extends BotModel {
  public SimpleBot(Map<String, Object> botSettings) {
    load(botSettings);
  }
}
