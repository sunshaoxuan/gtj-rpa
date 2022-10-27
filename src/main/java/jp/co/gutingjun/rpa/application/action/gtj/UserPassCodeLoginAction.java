package jp.co.gutingjun.rpa.application.action.gtj;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import jp.co.gutingjun.common.util.JsonUtils;
import jp.co.gutingjun.rpa.common.RPAConst;
import jp.co.gutingjun.rpa.model.action.WebClientActionModel;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class UserPassCodeLoginAction extends WebClientActionModel {
  public UserPassCodeLoginAction() {
    getContext().put(RPAConst.URL, "https://travel-sit.gutingjun.com/api/uaa/oauth/token");
    setAccessToken("d2ViQXBwOndlYkFwcA==");
  }

  @Override
  protected Object doAction() {
    try {
      WebRequest request =
          new WebRequest(
              new java.net.URL((String) getContext().get(RPAConst.URL)), HttpMethod.POST);
      List<NameValuePair> data = new ArrayList<>();
      data.add(new NameValuePair("username", (String) getContext().get(RPAConst.USERNAME)));
      data.add(new NameValuePair("password", (String) getContext().get(RPAConst.PASSWORD)));
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
          getTopContainer()
              .getContext()
              .put(RPAConst.ACCESSTOKEN, result.get(RPAConst.TAG_COOKIE_ACCESSTOKEN));
          setOutputData(true);
        } else {
          setOutputData(false);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }

    return getOutputData();
  }
}
