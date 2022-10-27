package jp.co.gutingjun.rpa.model.action;

import java.io.Serializable;
import java.util.List;

public abstract class AbsoluteActionFetcher implements Serializable {
  protected abstract List<IAction> getActionList();

  /**
   * 获取Action实例
   *
   * @param simpleName
   * @return
   */
  public IAction getAction(String simpleName) {
    try {
      return getActionList().stream()
          .filter(actionBean -> actionBean.getClass().getSimpleName().equalsIgnoreCase(simpleName))
          .findFirst()
          .get();
    } catch (Exception ex) {
      throw new RuntimeException(ex.getMessage());
    }
  }
}