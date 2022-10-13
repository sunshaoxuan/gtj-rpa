package jp.co.gutingjun.rpa.application.action.airhost;

import jp.co.gutingjun.rpa.common.RPAConst;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class OrderDetailFileFetcherActionTest {

  @Test
  void doAction() {
    UserPasswordLoginAction action = new UserPasswordLoginAction();
    action.getContext().put(RPAConst.URL, "https://cloud.airhost.co/accounts/sign_in");
    action.getContext().put(RPAConst.USERNAME, "sun.shaoxuan@51fanxing.co.jp");
    action.getContext().put(RPAConst.PASSWORD, "1qaz@WSX");
    action.getContext().put(RPAConst.LOADPAGE_AFTERLOGIN, "https://cloud.airhost.co/en/dashboard");
    action.execute();

    OrderDetailFileFetcherAction airHostAction = new OrderDetailFileFetcherAction();
    airHostAction.getContext().put(RPAConst.WEBCLIENT, action.getContext().get(RPAConst.WEBCLIENT));
    airHostAction.setBeginDate(
        LocalDate.parse("2022-08-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    airHostAction.setEndDate(
        LocalDate.parse("2022-08-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    Object outputData = airHostAction.execute();
    airHostAction.getWebClient().close();
  }
}
