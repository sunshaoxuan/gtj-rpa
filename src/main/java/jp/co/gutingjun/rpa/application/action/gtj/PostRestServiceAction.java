package jp.co.gutingjun.rpa.application.action.gtj;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import jp.co.gutingjun.common.util.JsonUtils;
import jp.co.gutingjun.rpa.common.RPAConst;
import jp.co.gutingjun.rpa.model.action.web.WebClientActionModel;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@Component
public class PostRestServiceAction extends WebClientActionModel {
  private String token;

  public PostRestServiceAction() {
    appendDependActionClasses(UserPassCodeLoginAction.class);
  }

  @Override
  public String getAccessToken() {
    if (!StringUtils.isBlank(super.getAccessToken())) {
      token = super.getAccessToken();
    } else if (getContext().containsKey(RPAConst.ACCESSTOKEN)) {
      token = (String) getContext().get(RPAConst.ACCESSTOKEN);
    } else if (getContext().get(RPAConst.WEBDRIVER) != null) {
      // 如果上下文中存在WebDriver，说明从上节点已经通过了认证
      RemoteWebDriver driver = (RemoteWebDriver) getContext().get(RPAConst.WEBDRIVER);
      token = driver.manage().getCookieNamed(RPAConst.TAG_COOKIE_ACCESSTOKEN).getValue();
    } else if (getWebClient() != null) {
      token =
          getWebClient().getCookieManager().getCookie(RPAConst.TAG_COOKIE_ACCESSTOKEN).getValue();
    } else {
      token = null;
    }

    setAccessToken(token);
    return token;
  }

  @Override
  protected Object doAction(Object inputData) {
    try {
      WebRequest request =
          new WebRequest(
              new java.net.URL((String) getContext().get(RPAConst.URL)), HttpMethod.POST);
      request.setRequestBody(
          JsonUtils.map2JSON(
              new ArrayList<Map<String, Object>>(
                  Arrays.asList((Map<String, Object>) getInputData()))));
      request.setAdditionalHeader("Authorization", "Bearer " + getAccessToken());
      request.setAdditionalHeader("Content-Type", "application/json");
      UnexpectedPage page = getWebClient().getPage(request);
      getWebClient().waitForBackgroundJavaScript(10000);
      WebResponse response = page.getWebResponse();
      if (response.getContentType().equals("application/json")) {
        response = page.getWebResponse();
        String json = response.getContentAsString();
        Map result = JsonUtils.json2Map(json);
        return result;
      }
    } catch (Exception ex) {
      getWebClient().close();
      throw new RuntimeException(ex.getMessage());
    }

    return null;
  }

  @Override
  public void validate(Object inputData) throws Exception {
    super.validate(inputData);

    if (getInputData() == null) {
      throw new RuntimeException("输入数据不能为空。");
    }

    if (!(getInputData() instanceof Map)) {
      throw new RuntimeException("输入数据的格式错误。");
    }
  }
}
