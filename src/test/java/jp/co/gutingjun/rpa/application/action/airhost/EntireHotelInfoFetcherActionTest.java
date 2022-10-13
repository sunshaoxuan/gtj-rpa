package jp.co.gutingjun.rpa.application.action.airhost;

import jp.co.gutingjun.rpa.common.RPAConst;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Map;

class EntireHotelInfoFetcherActionTest {

  @Test
  void doAction() {
    UserPasswordLoginAction action = new UserPasswordLoginAction();
    action.getContext().put(RPAConst.URL, "https://cloud.airhost.co/accounts/sign_in");
    action.getContext().put(RPAConst.USERNAME, "sun.shaoxuan@51fanxing.co.jp");
    action.getContext().put(RPAConst.PASSWORD, "1qaz@WSX");
    action.getContext().put(RPAConst.LOADPAGE_AFTERLOGIN, "https://cloud.airhost.co/en/dashboard");
    action.execute();

    EntireHotelInfoFetcherAction airHostAction = new EntireHotelInfoFetcherAction();
    airHostAction.getContext().put(RPAConst.WEBCLIENT, action.getContext().get(RPAConst.WEBCLIENT));
    Map outputData = (Map) airHostAction.execute();
    Assert.assertEquals(true, outputData != null && outputData.size() > 0);
    airHostAction.getWebClient().close();
  }

  @Test
  void doSingleHouseAction() {
    UserPasswordLoginAction action = new UserPasswordLoginAction();
    action.getContext().put(RPAConst.URL, "https://cloud.airhost.co/accounts/sign_in");
    action.getContext().put(RPAConst.USERNAME, "sun.shaoxuan@51fanxing.co.jp");
    action.getContext().put(RPAConst.PASSWORD, "1qaz@WSX");
    action.getContext().put(RPAConst.LOADPAGE_AFTERLOGIN, "https://cloud.airhost.co/en/dashboard");
    action.execute();

    if (action.getOutputData().equals(Boolean.TRUE)) {
      EntireHotelInfoFetcherAction airHostAction = new EntireHotelInfoFetcherAction();
      airHostAction
          .getContext()
          .put(RPAConst.WEBCLIENT, action.getContext().get(RPAConst.WEBCLIENT));
      airHostAction.setHouseId(191293L);
      Map outputData = (Map) airHostAction.execute();
      Assert.assertEquals(true, outputData != null && outputData.size() > 0);
      airHostAction.getWebClient().close();
    }
  }
}
