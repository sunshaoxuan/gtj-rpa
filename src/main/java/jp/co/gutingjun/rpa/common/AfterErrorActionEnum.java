package jp.co.gutingjun.rpa.common;

import java.util.Arrays;
import java.util.stream.Collectors;

/** 错误处理方式 */
public enum AfterErrorActionEnum {
  /** 暂停 */
  PAUSE("PAUSE", "暂停"),
  /** 停止 */
  STOP("STOP", "停止"),
  /** 执行Action */
  ACTION("ACTION", "动作");

  private String code;
  private String name;

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

  AfterErrorActionEnum(String code, String name) {
    this.code = code;
    this.name = name;
  }

  public static AfterErrorActionEnum getAfterErrorActionEnum(String code) {
    return Arrays.stream(AfterErrorActionEnum.values())
        .filter(cycleTypeEnum -> cycleTypeEnum.getCode().equals(code))
        .findFirst()
        .get();
  }

  public static AfterErrorActionEnum[] getAllEnum() {
    return Arrays.stream(AfterErrorActionEnum.values())
        .collect(Collectors.toList())
        .toArray(new AfterErrorActionEnum[0]);
  }
}