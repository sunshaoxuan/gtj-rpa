package jp.co.gutingjun.rpa.model.action.web;

import jp.co.gutingjun.rpa.common.RPAConst;
import jp.co.gutingjun.rpa.model.action.base.ActionModel;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class WebActionModel extends ActionModel {
  /** 访问凭证 */
  private String accessToken;

  /** 设置URL */
  public void setURL(String url) {
    getContext().put(RPAConst.URL, url);
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public Object execute() {
    // 传递上下文
    return super.execute();
  }

  @Override
  public void validate(Object inputData) throws Exception {
    AtomicBoolean exists = new AtomicBoolean(false);
    if (getContext() != null && getContext().size() > 0) {
      getContext()
          .forEach(
              (key, value) -> {
                if (key.equalsIgnoreCase(RPAConst.URL)) {
                  if (!StringUtils.isBlank(String.valueOf(value))) {
                    exists.set(true);
                  }
                }
              });
    }
    if (!exists.get()) {
      throw new RuntimeException("未指定要访问的网页地址。");
    }
  }
}
