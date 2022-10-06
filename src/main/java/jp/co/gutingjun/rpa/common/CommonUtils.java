package jp.co.gutingjun.rpa.common;

import jp.co.gutingjun.common.util.ObjectUtils;
import jp.co.gutingjun.common.util.SpringBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

import java.util.HashMap;
import java.util.Map;

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
   * 根据路径从深层Map中取值<br>
   * 路径格式：key1.key2.key3...
   *
   * @param dataMap
   * @param keys
   * @return
   */
  public static Object getMapValueByPath(Map dataMap, String keys) {
    if (!keys.contains(".")) {
      if (dataMap.containsKey(keys)) {
        return dataMap.get(keys);
      }
    }
    return getMapValueByPath(dataMap, keys.split("."));
  }

  /**
   * 根据路径从深层dataMap中取值
   *
   * @param dataMap 数据源Map
   * @param keys 路径Key集合
   * @return
   */
  public static Object getMapValueByPath(Map dataMap, String[] keys) {
    if (keys.length > 1) {
      if (dataMap.containsKey(keys[0]) && dataMap.get(keys[0]) instanceof Map) {
        String[] newKeys = new String[keys.length - 1];
        System.arraycopy(keys, 1, newKeys, 0, keys.length - 1);
        return getMapValueByPath((Map) dataMap.get(keys[0]), newKeys);
      }
    } else if (keys.length == 1) {
      if (dataMap.containsKey(keys[0])) {
        return dataMap.get(keys[0]);
      }
    }
    return null;
  }

  /**
   * Map的深层添加
   *
   * @param targetMap 目标Map
   * @param dataMap 数据源Map
   */
  public static void mapPutAll(Map<String, Object> targetMap, Map<String, Object> dataMap) {
    if (dataMap != null && dataMap.size() > 0) {
      dataMap.forEach(
          (dataKey, dataValue) -> {
            if (!targetMap.containsKey(dataKey)) {
              targetMap.put(dataKey, dataValue);
            } else {
              if (dataValue instanceof Map) {
                mapPutAll((Map) targetMap.get(dataKey), (Map) dataValue);
              } else {
                targetMap.put(dataKey, dataValue);
              }
            }
          });
    }
  }

  /**
   * 用多层key构建Map嵌套结构
   *
   * @param keys
   * @param value
   * @return
   */
  public static Map getDataMap(String[] keys, String value) {
    Map result = new HashMap();
    if (keys.length > 1) {
      String[] newKeys = new String[keys.length - 1];
      System.arraycopy(keys, 1, newKeys, 0, keys.length - 1);
      result.put(keys[0], getDataMap(newKeys, value));
    } else {
      result.put(keys[0], value);
    }

    return result;
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
