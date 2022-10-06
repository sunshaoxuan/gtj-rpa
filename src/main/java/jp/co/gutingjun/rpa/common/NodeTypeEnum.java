package jp.co.gutingjun.rpa.common;

import java.util.Arrays;
import java.util.stream.Collectors;

/** 节点类型枚举 */
public enum NodeTypeEnum {
  /** 开始节点 */
  BEGIN("BEGIN", "开始节点"),
  /** 结束节点 */
  END("END", "结束节点"),
  /** 连接节点 */
  LINKNODE("LINKNODE", "连接节点"),
  /** 任务节点 */
  JOBNODE("JOBNODE", "工作节点");

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

  NodeTypeEnum(String code, String name) {
    this.code = code;
    this.name = name;
  }

  public static NodeTypeEnum getNodeTypeEnum(String code) {
    return Arrays.stream(NodeTypeEnum.values())
        .filter(cycleTypeEnum -> cycleTypeEnum.getCode().equals(code))
        .findFirst()
        .get();
  }

  public static NodeTypeEnum[] getAllEnum() {
    return Arrays.stream(NodeTypeEnum.values())
        .collect(Collectors.toList())
        .toArray(new NodeTypeEnum[0]);
  }
}