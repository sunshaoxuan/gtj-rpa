package jp.co.gutingjun.rpa.application.action.gtj;

import jp.co.gutingjun.rpa.model.action.web.WebDriverActionModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

@Component
public class UserPassCodeLoginAction extends WebDriverActionModel {
  /** 登录后加载页面 */
  public static String LOADPAGE_AFTERLOGIN = "LoadPageAfterLogin";

  /** 用户Email */
  private String userEmail;
  /** 默认登录验证码 */
  private String defaultPasscode;

  public UserPassCodeLoginAction() {
    getWebContext().put(URL, "https://test.admin.gutingjun.com/login");
    getWebContext().put(LOADPAGE_AFTERLOGIN, "https://test.admin.gutingjun.com/index");
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
      setMoveOutOfScreen(true);
      getWebDriver().get((String) getWebContext().get(URL));
      WebElement form = getWebDriver().findElement(By.tagName("form"));
      form.findElements(By.tagName("input"))
          .forEach(
              element -> {
                if (element != null) {
                  if (element.getAttribute("type").equals("email")) {
                    element.sendKeys(getUserEmail());
                  } else if (element.getAttribute("type").equals("number")) {
                    element.sendKeys(getDefaultPasscode());
                  }
                }
              });

      form.findElements(By.tagName("button")).stream()
          .filter(
              element ->
                  element.getAttribute("class").contains("button-login")
                      || element.getAttribute("class").contains("button--primary"))
          .findFirst()
          .get()
          .click();
      Thread.sleep(3000L);
      getWebDriver().get((String) getWebContext().get(LOADPAGE_AFTERLOGIN));

      if (getWebDriver().findElementsByTagName("li").stream()
              .filter(li -> li.getAttribute("class").contains("submenu"))
              .count()
          > 0) {
        setOutputData(Boolean.TRUE);
      } else {
        setOutputData(Boolean.FALSE);
      }
      getWebContext().put(WEBDRIVER, getWebDriver());
      return getOutputData();
    } catch (Exception ex) {
      throw new RuntimeException(ex.getMessage());
    }
  }
}
