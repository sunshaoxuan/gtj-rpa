package jp.co.gutingjun.rpa.application.action.airhost;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Map;

class HotelFetcherActionTest {

  @Test
  void doAction() {
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

    HotelFetcherAction airHostAction = new HotelFetcherAction();
    airHostAction.setContext(action.getContext());
    Object outputData = airHostAction.execute();
    Assert.assertEquals(true, outputData != null && outputData instanceof Map);
  }
}