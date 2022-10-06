package jp.co.gutingjun.rpa.model.action.base;

import jp.co.gutingjun.rpa.common.CommonUtils;
import jp.co.gutingjun.rpa.common.OnErrorHandleEnum;

import java.util.*;
import java.util.stream.Collectors;

public abstract class ActionModel implements IAction {
  private final Long id;

  private Object inputData;

  private Object outputData;

  /** 发生错误处理方式 */
  private OnErrorHandleEnum onErrorHanleMethod;

  private Map<String, Object> context;

  private List<Class<? extends IAction>> dependActionClasses;

  public ActionModel() {
    id = CommonUtils.getNextID();
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public Object getInputData() {
    return inputData;
  }

  public void setInputData(Object inputData) {
    this.inputData = inputData;
  }

  public OnErrorHandleEnum getOnErrorHanleMethod() {
    return onErrorHanleMethod;
  }

  public void setOnErrorHanleMethod(OnErrorHandleEnum onErrorHanleMethod) {
    this.onErrorHanleMethod = onErrorHanleMethod;
  }

  @Override
  public Object getOutputData() {
    return outputData;
  }

  public void setOutputData(Object outputData) {
    this.outputData = outputData;
  }

  @Override
  public Map<String, Object> getContext() {
    if (context == null) {
      context = new HashMap<>();
    }
    return context;
  }

  @Override
  public void setContext(Map<String, Object> context) {
    this.context = context;
  }

  public Class<IAction>[] getDependActionClasses() {
    if (dependActionClasses == null) {
      dependActionClasses = new ArrayList<>();
    }
    return (Class<IAction>[]) dependActionClasses.toArray(new Class[0]);
  }

  public void setDependActionClasses(Class<IAction>[] dependActionClasses) {
    this.dependActionClasses = Arrays.stream(dependActionClasses).collect(Collectors.toList());
  }

  /**
   * 增加依赖动作
   *
   * @param actionClass
   */
  public void appendDependActionClasses(Class<? extends IAction> actionClass) {
    if (dependActionClasses == null) {
      dependActionClasses = new ArrayList<>();
    }

    dependActionClasses.add(actionClass);
  }

  /**
   * 执行动作前事件处理
   *
   * @return
   */
  protected Object beforeDoAction(Object inputData) {
    return inputData;
  }

  /**
   * 执行动作
   *
   * @return
   */
  protected abstract Object doAction(Object inputData);

  /**
   * 执行动作后事件处理
   *
   * @return
   */
  protected Object afterDoAction(Object outputData) {
    return outputData;
  }

  public Object execute() {
    // 执行动作前事件处理
    setInputData(beforeDoAction(getInputData()));

    try {
      validate(getInputData());
    } catch (Throwable ex) {
      throw new RuntimeException(ex.getMessage());
    }

    // 执行动作
    Object outputData = doAction(getInputData());

    // 执行动作后事件处理
    outputData = afterDoAction(outputData);

    setOutputData(outputData);

    return outputData;
  }
}
