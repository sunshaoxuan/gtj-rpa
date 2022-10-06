package jp.co.gutingjun.rpa.application.action.gtj;

import jp.co.gutingjun.rpa.model.action.web.WebActionModel;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

class UserPassCodeLoginActionTest {

  @Test
  public void doAction() {
    UserPassCodeLoginAction action = new UserPassCodeLoginAction();
    action.getWebContext().put(WebActionModel.URL, "https://travel-sit.gutingjun.com/login");
    action.setUserEmail("sun.shaoxuan@51fanxing.co.jp");
    action.setDefaultPasscode("2981");
    boolean rtn = (boolean) action.execute();
    action.getWebDriver().close();
    Assert.assertTrue(rtn);
  }
}
