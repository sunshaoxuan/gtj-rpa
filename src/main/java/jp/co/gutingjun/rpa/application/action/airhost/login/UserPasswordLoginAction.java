package jp.co.gutingjun.rpa.application.action.airhost.login;

import com.gargoylesoftware.htmlunit.html.*;
import jp.co.gutingjun.rpa.common.RPAConst;
import jp.co.gutingjun.rpa.model.action.WebClientActionModel;
import org.springframework.stereotype.Component;

/** AirHost动作：用户登录动作 */
@Component
public class UserPasswordLoginAction extends WebClientActionModel {

  public UserPasswordLoginAction() {
    getContext().put(RPAConst.LOGIN_FORM_ID, "new_user");
    getContext().put(RPAConst.USERNAME_FIELD_ID, "user_username");
    getContext().put(RPAConst.PASSWORD_FIELD_ID, "user_password");
  }

  @Override
  protected Object beforeDoAction(Object inputData) {
    return super.beforeDoAction(inputData);
  }

  @Override
  protected Object doAction() {
    try {
      setOutputData(Boolean.FALSE);

      boolean skipLogin = false;
      HtmlPage page = getWebClient().getPage((String) getContext().get(RPAConst.URL));
      if (page.getElementsByTagName("a").stream().count() > 0) {
        if (page.getElementsByTagName("a").get(0).getTextContent().equals("redirected")) {
          skipLogin = true;
        }
      }

      if (!skipLogin) {
        HtmlForm form = getFormByIDOrName(page, (String) getContext().get(RPAConst.LOGIN_FORM_ID));
        if (form == null) {
          throw new RuntimeException("找不到登录表单 [" + getContext().get(RPAConst.LOGIN_FORM_ID) + "]");
        }

        HtmlInput usernameInput =
            getFormInputByID(form, (String) getContext().get(RPAConst.USERNAME_FIELD_ID));
        if (usernameInput == null) {
          throw new RuntimeException("找不到用户名输入框");
        }
        usernameInput.setValueAttribute((String) getContext().get(RPAConst.USERNAME));

        HtmlInput passwordInput =
            getFormInputByID(form, (String) getContext().get(RPAConst.PASSWORD_FIELD_ID));
        if (passwordInput == null) {
          throw new RuntimeException("找不到密码输入框");
        }
        passwordInput.setValueAttribute((String) getContext().get(RPAConst.PASSWORD));

        HtmlButton btn = (HtmlButton) getHtmlElement(form, "button", "type", "submit");
        if (btn == null) {
          throw new RuntimeException("找不到登录提交按钮");
        }

        btn.click();
      }

      page = getWebClient().getPage((String) getContext().get(RPAConst.LOADPAGE_AFTERLOGIN));
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
            .equals(getContext().get(RPAConst.USERNAME))) {
          setOutputData(Boolean.TRUE);
        }
      }

      getContext().put(RPAConst.WEBCLIENT, getWebClient());
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
  public void validate() throws Exception {
    super.validate();

    if (!getContext().containsKey(RPAConst.LOGIN_FORM_ID)) {
      throw new RuntimeException("环境变量中应定义登录表单ID [UserPasswordLoginAction.LOGIN_FORM_ID]。");
    }

    if (!getContext().containsKey(RPAConst.USERNAME_FIELD_ID)) {
      throw new RuntimeException("环境变量中应定义用户名输入字段ID [UserPasswordLoginAction.USERNAME_FIELD_ID]");
    }

    if (!getContext().containsKey(RPAConst.PASSWORD_FIELD_ID)) {
      throw new RuntimeException("环境变量中应定义密码输入字段ID [UserPasswordLoginAction.PASSWORD_FIELD_ID]");
    }

    if (!getContext().containsKey(RPAConst.USERNAME)) {
      throw new RuntimeException("环境变量中应指定用户名称 [UserPasswordLoginAction.USERNAME]");
    }

    if (!getContext().containsKey(RPAConst.PASSWORD)) {
      throw new RuntimeException("环境变量中应指定用户密码 [UserPasswordLoginAction.PASSWORD]");
    }

    if (!getContext().containsKey(RPAConst.LOADPAGE_AFTERLOGIN)) {
      throw new RuntimeException("环境变量中应指定登录后加载页面 [UserPasswordLoginAction.LOADPAGE_AFTERLOGIN]");
    }
  }
}
