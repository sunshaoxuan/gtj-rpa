package jp.co.gutingjun.rpa.application.action.gtj;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import jp.co.gutingjun.common.util.JsonUtils;
import jp.co.gutingjun.rpa.common.RPAConst;
import jp.co.gutingjun.rpa.model.action.web.WebClientActionModel;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class UserPassCodeLoginAction extends WebClientActionModel {
  /** 用户Email */
  private String userEmail;
  /** 默认登录验证码 */
  private String defaultPasscode;

  public UserPassCodeLoginAction() {
    getContext().put(RPAConst.URL, "https://travel-sit.gutingjun.com/api/uaa/oauth/token");
    setAccessToken("d2ViQXBwOndlYkFwcA==");
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public String getDefaultPasscode() {
    return defaultPasscode;
  }

  public void setDefaultPasscode(String defaultPasscode) {
    this.defaultPasscode = defaultPasscode;
  }

  @Override
  protected Object doAction(Object inputData) {
    try {
      WebRequest request =
          new WebRequest(
              new java.net.URL((String) getContext().get(RPAConst.URL)), HttpMethod.POST);
      List<NameValuePair> data = new ArrayList<>();
      data.add(new NameValuePair("username", getUserEmail()));
      data.add(new NameValuePair("password", getDefaultPasscode()));
      data.add(new NameValuePair("grant_type", "password"));
      data.add(new NameValuePair("scope", "app"));
      data.add(new NameValuePair("auth_type", "email"));
      data.add(new NameValuePair("device", "kanran"));
      request.setRequestParameters(data);
      request.setAdditionalHeader("Authorization", "Basic " + getAccessToken());
      UnexpectedPage page = getWebClient().getPage(request);
      getWebClient().waitForBackgroundJavaScript(10000);
      WebResponse response = page.getWebResponse();
      if (response.getContentType().equals("application/json")) {
        response = page.getWebResponse();
        String json = response.getContentAsString();
        Map result = JsonUtils.json2Map(json);
        if (result.containsKey(RPAConst.TAG_COOKIE_ACCESSTOKEN)) {
          getContext().put(RPAConst.ACCESSTOKEN, result.get(RPAConst.TAG_COOKIE_ACCESSTOKEN));
          return true;
        } else {
          return false;
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }

    return null;
  }
}
