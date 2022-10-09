package jp.co.gutingjun.rpa.application.action;

import jp.co.gutingjun.rpa.common.PackageClassLoader;
import jp.co.gutingjun.rpa.model.action.base.AbsoluteActionFetcher;
import jp.co.gutingjun.rpa.model.action.base.IAction;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class BaseActionFetcher extends AbsoluteActionFetcher {
  private final List<IAction> actionList = new ArrayList<IAction>();

  @Override
  protected List<IAction> getActionList() {
    if (actionList.size() == 0) {
      PackageClassLoader.getClass("jp.co.gutingjun.rpa.application.action", true).stream()
          .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
          .forEach(
              clazz -> {
                try {
                  Object newIns = clazz.newInstance();
                  if (newIns instanceof IAction) {
                    actionList.add((IAction) newIns);
                  }
                } catch (InstantiationException e) {
                  throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                  throw new RuntimeException(e);
                }
              });
    }
    return actionList;
  }
}
