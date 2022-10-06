package jp.co.gutingjun.rpa.application.action.gtj;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import jp.co.gutingjun.common.util.JsonUtils;
import jp.co.gutingjun.rpa.model.action.web.WebClientActionModel;
import jp.co.gutingjun.rpa.model.action.web.WebDriverActionModel;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@Component
public class PostRestServiceAction extends WebClientActionModel {
  public static final String TAG_COOKIE_ACCESSTOKEN = "access_token";

  public PostRestServiceAction() {
    appendDependActionClasses(UserPassCodeLoginAction.class);
  }

  private String token;

  @Override
  public String getAccessToken() {
    if (getWebContext().get(WebDriverActionModel.WEBDRIVER) != null) {
      // 如果上下文中存在WebDriver，说明从上节点已经通过了认证
      RemoteWebDriver driver =
          (RemoteWebDriver) getWebContext().get(WebDriverActionModel.WEBDRIVER);
      token = driver.manage().getCookieNamed(TAG_COOKIE_ACCESSTOKEN).getValue();
    } else if (getWebClient() != null) {
      token = getWebClient().getCookieManager().getCookie(TAG_COOKIE_ACCESSTOKEN).getValue();
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
          new WebRequest(new java.net.URL((String) getWebContext().get(URL)), HttpMethod.POST);
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