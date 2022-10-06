package jp.co.gutingjun.rpa.common;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum OnErrorHandleEnum {
  BREAK("break", "中断"),
  CONTINUE("continue", "继续"),
  RUNACTION("runaction", "执行动作");

  private String code;
  private String name;

  OnErrorHandleEnum(String code, String name) {
    this.code = code;
    this.name = name;
  }

  public static OnErrorHandleEnum getOnErrorHandleEnum(String code) {
    return Arrays.stream(OnErrorHandleEnum.values())
        .filter(cycleTypeEnum -> cycleTypeEnum.getCode().equals(code))
        .findFirst()
        .get();
  }

  public static OnErrorHandleEnum[] getAllEnum() {
    return Arrays.stream(OnErrorHandleEnum.values())
        .collect(Collectors.toList())
        .toArray(new OnErrorHandleEnum[0]);
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
