package jp.co.gutingjun.rpa.application.action.airhost;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import jp.co.gutingjun.rpa.application.action.airhost.login.UserPasswordLoginAction;
import jp.co.gutingjun.rpa.common.RPAConst;
import org.junit.jupiter.api.Test;
import org.wildfly.common.Assert;

class UserPasswordLoginActionTest {

  @Test
  void doAction() {
    UserPasswordLoginAction action = new UserPasswordLoginAction();
    action.getContext().put(RPAConst.URL, "https://cloud.airhost.co/accounts/sign_in");
    action.getContext().put(RPAConst.USERNAME, "sun.shaoxuan@51fanxing.co.jp");
    action.getContext().put(RPAConst.PASSWORD, "1qaz@WSX");
    action.getContext().put(RPAConst.LOADPAGE_AFTERLOGIN, "https://cloud.airhost.co/en/dashboard");
    action.execute();

    Object outputData = action.getOutputData();
    if (outputData instanceof HtmlPage) {
      Assert.assertTrue(
          ((HtmlPage) outputData)
                  .getElementsByTagName("img").stream()
                      .filter(
                          ele ->
                              ele.getAttribute("alt")
                                  .equals(action.getContext().get(RPAConst.USERNAME)))
                      .count()
              > 0);
    }
  }
}
