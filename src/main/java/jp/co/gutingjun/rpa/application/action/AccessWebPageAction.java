package jp.co.gutingjun.rpa.application.action;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import jp.co.gutingjun.rpa.common.RPAConst;
import jp.co.gutingjun.rpa.model.action.WebClientActionModel;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用页面访问动作
 *
 * @author sunsx
 * */
@Component
public class AccessWebPageAction extends WebClientActionModel {
  @Override
  protected Object beforeDoAction(Object inputData) {
    return super.beforeDoAction(inputData);
  }

  @Override
  protected Object doAction() {
    String result = null;
    try {
      HtmlPage page = getWebClient().getPage((String) getContext().get(RPAConst.URL));
      getContext().put(RPAConst.WEBCLIENT, getWebClient());
      setOutputData(page);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    return result;
  }

  @Override
  protected Object afterDoAction(Object outputData) {
    Map<String, Object> outputDataMap = new HashMap<>();
    outputDataMap.putAll(getContext());
    outputDataMap.put(RPAConst.OUTPUTDATA, outputData);
    return outputDataMap;
  }

  @Override
  public void validate() throws Exception {
    super.validate();
  }
}
