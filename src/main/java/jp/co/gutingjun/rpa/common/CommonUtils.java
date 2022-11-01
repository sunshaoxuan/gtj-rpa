package jp.co.gutingjun.rpa.common;

import jp.co.gutingjun.common.util.ObjectUtils;
import jp.co.gutingjun.common.util.SpringBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

@Slf4j
public class CommonUtils {
  public static Long getNextID() {
    Object snowflakeID = getBean(RPAConst.SNOWFLAKE_INSTANCENAME);
    return (Long) ObjectUtils.invokeMethod(snowflakeID, RPAConst.SNOWFLAKE_NEXTID);
  }

  private static Object getBean(String entityClassSimpleName) {
    if (SpringBeanUtil.getApplicationContext() != null) {
      for (String beanDefinitionName :
          SpringBeanUtil.getApplicationContext().getBeanDefinitionNames()) {
        if (entityClassSimpleName.equals(RPAConst.SNOWFLAKE_INSTANCENAME)) {
          if (SpringBeanUtil.getApplicationContext()
              .getBean(beanDefinitionName)
              .getClass()
              .getName()
              .toUpperCase()
              .contains("SNOWFLAKEID")) {
            return SpringBeanUtil.getApplicationContext().getBean(beanDefinitionName);
          }
        } else if (beanDefinitionName
            .toUpperCase()
            .contains(entityClassSimpleName.toUpperCase() + RPAConst.MAPPER.toUpperCase())) {
          return SpringBeanUtil.getApplicationContext().getBean(beanDefinitionName);
        }
      }
    }

    return null;
  }

  /**
   * 线程休眠
   *
   * @param maxSleepSeconds
   */
  public static void sleepRandomSeconds(int maxSleepSeconds) {
    try {
      int waitSecs = RandomUtils.nextInt(1, maxSleepSeconds);
      Thread.sleep(waitSecs * 1000L);
    } catch (Exception ex) {
    }
  }
}
