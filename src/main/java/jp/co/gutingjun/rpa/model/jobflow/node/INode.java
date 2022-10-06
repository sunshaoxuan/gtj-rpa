package jp.co.gutingjun.rpa.model.jobflow.node;

import jp.co.gutingjun.rpa.common.NodeTypeEnum;

import java.io.Serializable;
import java.util.Map;

/** 环境变量载体 */
public interface INode  extends Serializable {
  /**
   * 获取节点ID
   *
   * @return
   */
  Long getId();

  /**
   * 获取当前节点标签
   *
   * @return
   */
  String getTag();

  /**
   * 设置当前节点标签
   *
   * @param tag
   */
  void setTag(String tag);

  /**
   * 获取节点类型
   *
   * @return
   */
  NodeTypeEnum getNoteType();

  /**
   * 获得环境变更集合
   *
   * @return
   */
  Map<String, Object> getContext();

  /**
   * 设置环境变更集体
   *
   * @param context
   */
  void setContext(Map<String, Object> context);

  /** 检查当前节点及相连节点有效性 */
  void validate() throws Exception;
}