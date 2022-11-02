package jp.co.gutingjun.rpa.model.log;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 公共日志服务
 *
 * @author sunsx
 * */
@Slf4j
public class Logger {
  /** 消息 */
  public static final String LOG_LEVEL = "INFO";
  /** 错误 */
  public static final String ERROR_LEVEL = "ERROR";

  private static final List<LogData> logDataList = new ArrayList<LogData>();

  /**
   * 记录消息
   *
   * @param data
   */
  public static void log(LogData data) {
    data.setLevel(LOG_LEVEL);
    logDataList.add(data);
  }

  /**
   * 记录错误
   *
   * @param data
   */
  public static void error(LogData data) {
    data.setLevel(ERROR_LEVEL);
    logDataList.add(data);
  }
}
