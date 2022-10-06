package jp.co.gutingjun.rpa.application.action.gtj;

import jp.co.gutingjun.common.util.JsonUtils;
import jp.co.gutingjun.rpa.common.CommonUtils;
import jp.co.gutingjun.rpa.model.action.web.WebActionModel;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

class PostRestServiceActionTest {

  @Test
  public void doAction() {
    UserPassCodeLoginAction loginAction = new UserPassCodeLoginAction();
    loginAction.setUserEmail("sun.shaoxuan@51fanxing.co.jp");
    loginAction.setDefaultPasscode("2981");
    boolean rtn = (boolean) loginAction.execute();

    PostRestServiceAction action = new PostRestServiceAction();
    action.getWebContext().put(WebActionModel.URL, "https://test.admin.gutingjun.com/api/hotel/pms/page");
    action.setInputData(JsonUtils.json2Map("{'pageCode':'MS1001','lang':'JP'}"));
    CommonUtils.mapPutAll(action.getWebContext(), loginAction.getWebContext());
    Map<String, Object> result = (Map<String, Object>) action.execute();
    loginAction.getWebDriver().close();
    action.getWebClient().close();
    System.out.println(JsonUtils.map2JSON(new ArrayList<Map<String, Object>>((Collection<? extends Map<String, Object>>) Arrays.asList(result))));
    Assert.assertEquals(true, result != null && result.containsKey("code") && result.get("code").equals("200"));
  }
}