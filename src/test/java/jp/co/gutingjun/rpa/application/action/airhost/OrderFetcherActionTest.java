package jp.co.gutingjun.rpa.application.action.airhost;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

class OrderFetcherActionTest {

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

    OrderFetcherAction airHostAction = new OrderFetcherAction();
    airHostAction.setContext(action.getContext());
    airHostAction.setBeginDate(LocalDate.of(2022, 8, 1));
    airHostAction.setEndDate(LocalDate.of(2022, 8, 31));
    Object outputData = airHostAction.execute();
    Assert.assertEquals(true, outputData != null && outputData instanceof Map);
  }
}
