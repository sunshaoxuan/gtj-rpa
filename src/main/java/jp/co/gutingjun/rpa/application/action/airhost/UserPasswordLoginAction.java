package jp.co.gutingjun.rpa.application.action.airhost;

import com.gargoylesoftware.htmlunit.html.*;
import jp.co.gutingjun.rpa.model.action.web.WebClientActionModel;
import org.springframework.stereotype.Component;

@Component
public class UserPasswordLoginAction extends WebClientActionModel {
  /** 登录表单ID */
  public static String LOGIN_FORM_ID = "LoginFormID";

  /** 用户名表单项ID */
  public static String USERNAME_FIELD_ID = "UserNameFieldID";

  /** 用户名称 */
  public static String USERNAME = "UserName";

  /** 密码表单项ID */
  public static String PASSWORD_FIELD_ID = "PasswordFieldID";

  /** 用户密码 */
  public static String PASSWORD = "Password";

  /** 登录后加载页面 */
  public static String LOADPAGE_AFTERLOGIN = "LoadPageAfterLogin";

  public UserPasswordLoginAction() {
    getWebContext().put(LOGIN_FORM_ID, "new_user");
    getWebContext().put(USERNAME_FIELD_ID, "user_username");
    getWebContext().put(PASSWORD_FIELD_ID, "user_password");
  }

  @Override
  protected Object beforeDoAction(Object inputData) {
    return super.beforeDoAction(inputData);
  }

  @Override
  protected Object doAction(Object inputData) {
    try {
      setOutputData(Boolean.FALSE);
      HtmlPage page = getWebClient().getPage((String) getWebContext().get(URL));
      HtmlForm form = getFormByIDOrName(page, (String) getWebContext().get(LOGIN_FORM_ID));
      if (form == null) {
        throw new RuntimeException("找不到登录表单 [" + getWebContext().get(LOGIN_FORM_ID) + "]");
      }

      HtmlInput usernameInput =
          getFormInputByID(form, (String) getWebContext().get(USERNAME_FIELD_ID));
      if (usernameInput == null) {
        throw new RuntimeException("找不到用户名输入框");
      }
      usernameInput.setValueAttribute((String) getWebContext().get(USERNAME));

      HtmlInput passwordInput =
          getFormInputByID(form, (String) getWebContext().get(PASSWORD_FIELD_ID));
      if (passwordInput == null) {
        throw new RuntimeException("找不到密码输入框");
      }
      passwordInput.setValueAttribute((String) getWebContext().get(PASSWORD));

      HtmlButton btn = (HtmlButton) getHtmlElement(form, "button", "type", "submit");
      if (btn == null) {
        throw new RuntimeException("找不到登录提交按钮");
      }

      btn.click();
      page = getWebClient().getPage((String) getWebContext().get(LOADPAGE_AFTERLOGIN));
      HtmlListItem lstItem =
          (HtmlListItem)
              page.getElementsByTagName("li").stream()
                  .filter(li -> li.getAttribute("class").startsWith("dropdown dropdown-user"))
                  .findFirst()
                  .get();
      if (lstItem.getElementsByTagName("span").size() == 1) {
        if (lstItem
            .getElementsByTagName("span")
            .get(0)
            .getTextContent()
            .equals(getWebContext().get(USERNAME))) {
          setOutputData(Boolean.TRUE);
        }
      }

      getContext().put(WEBCLIENT, getWebClient());
      return getOutputData();
    } catch (Exception ex) {
      throw new RuntimeException(ex.getMessage());
    }
  }

  @Override
  protected Object afterDoAction(Object outputData) {
    return super.afterDoAction(outputData);
  }

  @Override
  public void validate(Object inputData) throws Exception {
    super.validate(inputData);

    if (!getWebContext().containsKey(LOGIN_FORM_ID)) {
      throw new RuntimeException("环境变量中应定义登录表单ID [UserPasswordLoginAction.LOGIN_FORM_ID]。");
    }

    if (!getWebContext().containsKey(USERNAME_FIELD_ID)) {
      throw new RuntimeException("环境变量中应定义用户名输入字段ID [UserPasswordLoginAction.USERNAME_FIELD_ID]");
    }

    if (!getWebContext().containsKey(PASSWORD_FIELD_ID)) {
      throw new RuntimeException("环境变量中应定义密码输入字段ID [UserPasswordLoginAction.PASSWORD_FIELD_ID]");
    }

    if (!getWebContext().containsKey(USERNAME)) {
      throw new RuntimeException("环境变量中应指定用户名称 [UserPasswordLoginAction.USERNAME]");
    }

    if (!getWebContext().containsKey(PASSWORD)) {
      throw new RuntimeException("环境变量中应指定用户密码 [UserPasswordLoginAction.PASSWORD]");
    }

    if (!getWebContext().containsKey(LOADPAGE_AFTERLOGIN)) {
      throw new RuntimeException("环境变量中应指定登录后加载页面 [UserPasswordLoginAction.LOADPAGE_AFTERLOGIN]");
    }
  }
}
