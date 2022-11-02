package jp.co.gutingjun.rpa.application.action.gtj;

import jp.co.gutingjun.common.util.JsonUtils;
import jp.co.gutingjun.rpa.application.action.gtj.login.UserPassCodeLoginAction;
import jp.co.gutingjun.rpa.application.action.gtj.service.PostRestServiceAction;
import jp.co.gutingjun.rpa.common.RPAConst;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

class PostRestServiceActionTest {

  @Test
  public void doAction() {
    UserPassCodeLoginAction loginAction = new UserPassCodeLoginAction();
    loginAction.getContext().put(RPAConst.USERNAME, "sun.shaoxuan@51fanxing.co.jp");
    loginAction.getContext().put(RPAConst.PASSWORD, "2981");
    boolean rtn = (boolean) loginAction.execute();

    if (rtn) {
      PostRestServiceAction action = new PostRestServiceAction();
      action.getContext().put(RPAConst.WEBCLIENT, action.getContext().get(RPAConst.WEBCLIENT));
      action.getContext().put(RPAConst.URL, "https://test.admin.gutingjun.com/api/hotel/pms/page");
      action
          .getContext()
          .put(RPAConst.ACCESSTOKEN, loginAction.getContext().get(RPAConst.ACCESSTOKEN));
      action.setInputData(JsonUtils.json2Map("{'pageCode':'MS1001','lang':'JP'}"));
      Map<String, Object> result = (Map<String, Object>) action.execute();
      action.getWebClient().close();
      System.out.println(
          JsonUtils.map2JSON(new ArrayList<Map<String, Object>>(Arrays.asList(result))));
      Assert.assertEquals(
          true, result != null && result.containsKey("code") && result.get("code").equals("200"));
    } else {
      Assert.assertFalse(rtn);
    }
  }
}
