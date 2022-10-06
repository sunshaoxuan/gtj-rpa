package jp.co.gutingjun.rpa.application.bot;

import jp.co.gutingjun.rpa.model.bot.BotInstance;

import java.util.Map;

public class SimpleBot extends BotInstance {
    public SimpleBot(Map<String, Object> botSettings){
        load(botSettings);
    }
}
