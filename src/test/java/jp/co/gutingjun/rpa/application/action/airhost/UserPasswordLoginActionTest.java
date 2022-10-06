package jp.co.gutingjun.rpa.application.action.airhost;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;
import org.wildfly.common.Assert;

class UserPasswordLoginActionTest {

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

    Object outputData = action.getOutputData();
    if (outputData instanceof HtmlPage) {
      Assert.assertTrue(
          ((HtmlPage) outputData)
                  .getElementsByTagName("img").stream()
                      .filter(
                          ele ->
                              ele.getAttribute("alt")
                                  .equals(
                                      action.getWebContext().get(UserPasswordLoginAction.USERNAME)))
                      .count()
              > 0);
    }
  }
}
