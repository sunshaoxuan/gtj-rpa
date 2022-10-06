package jp.co.gutingjun.rpa.application.action.airhost;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Map;

class OrderDetailFetcherActionTest {

  @Test
  public void doAction() {
    UserPasswordLoginAction action = new UserPasswordLoginAction();
    action
        .getWebContext()
        .put(UserPasswordLoginAction.URL, "https://cloud.airhost.co/accounts/sign_in");
    action.getWebContext().put(UserPasswordLoginAction.USERNAME, "sun.shaoxuan@51fanxing.co.jp");
    action.getWebContext().put(UserPasswordLoginAction.PASSWORD, "1qaz@WSX");
    action
        .getWebContext()
        .put(UserPasswordLoginAction.LOADPAGE_AFTERLOGIN, "https://cloud.airhost.co/en/dashboard");
    action.execute();

    OrderDetailFetcherAction airHostAction = new OrderDetailFetcherAction();
    airHostAction.setContext(action.getContext());
    airHostAction.setOrderId(10610824L);
    Object outputData = airHostAction.execute();
    Assert.assertEquals(true, outputData != null && outputData instanceof Map);
  }
}
