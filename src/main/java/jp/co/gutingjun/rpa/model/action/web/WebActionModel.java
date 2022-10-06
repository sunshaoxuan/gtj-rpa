package jp.co.gutingjun.rpa.model.action.web;

import jp.co.gutingjun.rpa.model.action.base.ActionModel;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class WebActionModel extends ActionModel {
  /** 网页地址 */
  public static String URL = "URL";

  /** 输出数据 */
  public static String OUTPUTDATA = "OutputData";

  /** 访问凭证 */
  public static String ACCESSTOKEN = "AccessToKen";

  /** 设置URL */
  public void setURL(String url) {
    getWebContext().put(URL, url);
  }

  /** 访问凭证 */
  private String accessToken;

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  /** 网络环境变量 */
  private Map<String, Object> webContext;

  /**
   * 获取环境变量
   *
   * @return
   */
  public Map<String, Object> getWebContext() {
    if (webContext == null) {
      webContext = new HashMap<>();
    }

    return webContext;
  }

  public void setWebContext(Map<String, Object> webContext) {
    this.webContext = webContext;
  }

  @Override
  public void validate(Object inputData) throws Exception {
    if (!getWebContext().containsKey(URL)
        || StringUtils.isBlank(String.valueOf(getWebContext().get(URL)))) {
      throw new RuntimeException("未指定要访问的网页地址。");
    }
  }
}