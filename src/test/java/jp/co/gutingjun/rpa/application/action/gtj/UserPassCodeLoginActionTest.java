package jp.co.gutingjun.rpa.application.action.gtj;

import jp.co.gutingjun.rpa.application.action.gtj.login.UserPassCodeLoginAction;
import jp.co.gutingjun.rpa.common.RPAConst;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

class UserPassCodeLoginActionTest {

  @Test
  public void doAction() {
    UserPassCodeLoginAction action = new UserPassCodeLoginAction();
    action.getContext().put(RPAConst.USERNAME, "sun.shaoxuan@51fanxing.co.jp");
    action.getContext().put(RPAConst.PASSWORD, "2981");
    boolean rtn = (boolean) action.execute();
    action.getWebClient().close();
    Assert.assertTrue(rtn);
  }
}
