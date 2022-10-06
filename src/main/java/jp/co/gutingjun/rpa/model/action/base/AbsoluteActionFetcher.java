package jp.co.gutingjun.rpa.model.action.base;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbsoluteActionFetcher {
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
          .filter(
              actionBean ->
                  actionBean.getClass().getName().toUpperCase().contains(simpleName.toUpperCase()))
          .collect(Collectors.toList())
          .get(0);
    } catch (Exception ex) {
    }
    return null;
  }
}