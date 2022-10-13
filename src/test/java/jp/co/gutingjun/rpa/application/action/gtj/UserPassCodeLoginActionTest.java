package jp.co.gutingjun.rpa.application.action.gtj;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

class UserPassCodeLoginActionTest {

  @Test
  public void doAction() {
    UserPassCodeLoginAction action = new UserPassCodeLoginAction();
    action.setUserEmail("sun.shaoxuan@51fanxing.co.jp");
    action.setDefaultPasscode("2981");
    boolean rtn = (boolean) action.execute();
    action.getWebClient().close();
    Assert.assertTrue(rtn);
  }
}
