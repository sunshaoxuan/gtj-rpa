package jp.co.gutingjun.rpa.application.action;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class AccessWebPageActionTest {

  @Test
  void doAction() {
    AccessWebPageAction action = new AccessWebPageAction();
    Map<String, Object> webContext = new HashMap<>();
    webContext.put(AccessWebPageAction.URL, "https://cloud.airhost.co/accounts/sign_in");
    action.setWebContext(webContext);
    action.doAction(null);
    Assertions.assertNotEquals(null, action.getOutputData());
  }
}
