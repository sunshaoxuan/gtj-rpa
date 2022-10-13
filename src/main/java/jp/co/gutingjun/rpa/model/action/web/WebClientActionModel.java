package jp.co.gutingjun.rpa.model.action.web;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import jp.co.gutingjun.rpa.common.RPAConst;

public abstract class WebClientActionModel extends WebActionModel {

  /** 网络连接 */
  private WebClient webClient;

  /**
   * 获取网络连接上下文对象
   *
   * @return
   */
  public WebClient getWebClient() {
    if (webClient == null) {
      if (getContext().containsKey(RPAConst.WEBCLIENT)
          && getContext().get(RPAConst.WEBCLIENT) != null) {
        // 环境变量里有就从环境变更里取
        webClient = (WebClient) getContext().get(RPAConst.WEBCLIENT);
      } else {
        // 环境变更里没有就新建
        webClient = new WebClient(BrowserVersion.CHROME);
        setWebClientDefaultValue(webClient);
      }
    }

    return webClient;
  }

  /**
   * 设置网络连接上下文对象
   *
   * @param webClient
   */
  public void setWebClient(WebClient webClient) {
    this.webClient = webClient;
  }

  protected void setWebClientDefaultValue(WebClient webClient) {
    webClient.getOptions().setThrowExceptionOnScriptError(false);
    webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
    webClient.getOptions().setActiveXNative(false);
    webClient.getOptions().setCssEnabled(false);
    webClient.getOptions().setJavaScriptEnabled(true);
    webClient.getOptions().setRedirectEnabled(false);
    webClient.getOptions().setAppletEnabled(false);
    webClient.getOptions().setGeolocationEnabled(false);
    webClient.getOptions().setPopupBlockerEnabled(false);
    webClient.getOptions().setTimeout(10000);
    webClient.setAjaxController(new NicelyResynchronizingAjaxController());
  }

  protected HtmlInput getFormInputByID(HtmlForm form, String inputFieldID) {
    return (HtmlInput) getHtmlElement(form, "input", "id", inputFieldID);
  }

  protected HtmlForm getFormByIDOrName(HtmlPage page, String idOrName) {
    HtmlForm form = (HtmlForm) getHtmlElement(page.getDocumentElement(), "form", "id", idOrName);

    if (form == null) {
      form = (HtmlForm) getHtmlElement(page.getDocumentElement(), "form", "name", idOrName);
    }
    return form;
  }

  protected HtmlElement getHtmlElement(
      DomElement element, String tagName, String nodeName, String nodeValue) {
    return element.getElementsByTagName(tagName).stream()
        .filter(
            htmlInput ->
                htmlInput.getAttributeNode(nodeName) != null
                    && htmlInput.getAttributeNode(nodeName).getNodeValue().equals(nodeValue))
        .findFirst()
        .get();
  }
}
