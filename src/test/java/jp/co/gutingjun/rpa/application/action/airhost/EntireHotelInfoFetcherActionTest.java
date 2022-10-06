package jp.co.gutingjun.rpa.application.action.airhost;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Map;

class EntireHotelInfoFetcherActionTest {

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

    EntireHotelInfoFetcherAction airHostAction = new EntireHotelInfoFetcherAction();
    airHostAction.setContext(action.getContext());
    Map outputData = (Map) airHostAction.execute();
    Assert.assertEquals(true, outputData != null && outputData.size() > 0);
    airHostAction.getWebClient().close();
  }

  @Test
  void doSingleHouseAction() {
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

    if (action.getOutputData().equals(Boolean.TRUE)) {
      EntireHotelInfoFetcherAction airHostAction = new EntireHotelInfoFetcherAction();
      airHostAction.setContext(action.getContext());
      airHostAction.setHouseId(191293L);
      Map outputData = (Map) airHostAction.execute();
      Assert.assertEquals(true, outputData != null && outputData.size() > 0);
      airHostAction.getWebClient().close();
    }
  }
}
