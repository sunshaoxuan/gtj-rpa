package jp.co.gutingjun.rpa.common;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 频率类型枚举
 *
 * @author sunsx
 * */
public enum FrequencyTypeEnum implements Serializable {
  /** 按小时 */
  HOUR("HOUR", "小时"),
  /** 按分钟 */
  MINUTE("MINUTE", "分钟"),
  /** 按秒 */
  SECOND("SECOND", "秒"),
  /** 一次 */
  ONCE("ONCE", "一次");

  private String code;
  private String name;

  FrequencyTypeEnum(String code, String name) {
    this.code = code;
    this.name = name;
  }

  public static FrequencyTypeEnum getFrequencyTypeEnum(String code) {
    return Arrays.stream(FrequencyTypeEnum.values())
        .filter(cycleTypeEnum -> cycleTypeEnum.getCode().equals(code))
        .findFirst()
        .get();
  }

  public static FrequencyTypeEnum[] getAllEnum() {
    return Arrays.stream(FrequencyTypeEnum.values())
        .collect(Collectors.toList())
        .toArray(new FrequencyTypeEnum[0]);
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}