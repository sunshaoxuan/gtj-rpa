package jp.co.gutingjun.rpa.common;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 周期类型枚举
 *
 * @author sunsx
 */
public enum CycleTypeEnum implements Serializable {
  /** 按月 */
  MONTH("MONTH", "月"),
  /** 按周 */
  WEEK("WEEK", "周"),
  /** 按天 */
  DAY("DAY", "天");

  private String code;
  private String name;

  CycleTypeEnum(String code, String name) {
    this.code = code;
    this.name = name;
  }

  public static CycleTypeEnum getCycleTypeEnum(String code) {
    return Arrays.stream(CycleTypeEnum.values())
        .filter(cycleTypeEnum -> cycleTypeEnum.getCode().equals(code))
        .findFirst()
        .get();
  }

  public static CycleTypeEnum[] getAllEnum() {
    return Arrays.stream(CycleTypeEnum.values())
        .collect(Collectors.toList())
        .toArray(new CycleTypeEnum[0]);
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
