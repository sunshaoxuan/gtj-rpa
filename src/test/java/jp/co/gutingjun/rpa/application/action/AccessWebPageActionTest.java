package jp.co.gutingjun.rpa.application.action;

import jp.co.gutingjun.rpa.common.RPAConst;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class AccessWebPageActionTest {

  @Test
  void doAction() {
    AccessWebPageAction action = new AccessWebPageAction();
    Map<String, Object> webContext = new HashMap<>();
    webContext.put(RPAConst.URL, "https://cloud.airhost.co/accounts/sign_in");
    action.setContext(webContext);
    action.doAction(null);
    Assertions.assertNotEquals(null, action.getOutputData());
  }
}
