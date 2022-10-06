package jp.co.gutingjun.rpa.application.action.airhost;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class OrderDetailFileFetcherActionTest {

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

    OrderDetailFileFetcherAction airHostAction = new OrderDetailFileFetcherAction();
    airHostAction.setContext(action.getContext());
    airHostAction.setBeginDate(
        LocalDate.parse("2022-08-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    airHostAction.setEndDate(
        LocalDate.parse("2022-08-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    Object outputData = airHostAction.execute();
    airHostAction.getWebClient().close();
  }
}
