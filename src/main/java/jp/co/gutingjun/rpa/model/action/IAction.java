package jp.co.gutingjun.rpa.model.action;

import java.io.Serializable;
import java.util.Map;

public interface IAction extends Serializable {
  /** 获取工作ID */
  Long getId();

  /**
   * 获取依赖动作
   *
   * @return
   */
  Class<IAction>[] getDependActionClasses();

  /**
   * 设置依赖动作
   *
   * @param actions
   */
  void setDependActionClasses(Class<IAction>[] actions);

  /**
   * 获取输入数据
   *
   * @return
   */
  Object getInputData();

  /**
   * 设置输入数据
   *
   * @param inputData
   */
  void setInputData(Object inputData);

  /**
   * 获取输出数据
   *
   * @return
   */
  Object getOutputData();

  /**
   * 设置输出数据
   *
   * @param outputData
   */
  void setOutputData(Object outputData);

  /** 获取环境变量 */
  Map<String, Object> getContext();

  /**
   * 设置环境变量
   *
   * @param context
   */
  void setContext(Map<String, Object> context);

  /**
   * 执行检查方法
   *
   * @throws Exception
   */
  void validate() throws Exception;

  /** 执行动作 */
  Object execute();
}
