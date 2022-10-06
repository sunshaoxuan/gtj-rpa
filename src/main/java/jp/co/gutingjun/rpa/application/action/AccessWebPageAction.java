package jp.co.gutingjun.rpa.application.action;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import jp.co.gutingjun.rpa.model.action.web.WebClientActionModel;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AccessWebPageAction extends WebClientActionModel {
  @Override
  protected Object beforeDoAction(Object inputData) {
    return super.beforeDoAction(inputData);
  }

  @Override
  protected Object doAction(Object inputData) {
    String result = null;
    try {
      HtmlPage page = getWebClient().getPage((String) getWebContext().get(URL));
      getContext().put(WEBCLIENT, getWebClient());
      setOutputData(page);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    return result;
  }

  @Override
  protected Object afterDoAction(Object outputData) {
    Map<String, Object> outputDataMap = new HashMap<>();
    outputDataMap.putAll(getWebContext());
    outputDataMap.put(OUTPUTDATA, outputData);
    return outputDataMap;
  }

  @Override
  public void validate(Object inputData) throws Exception {
    super.validate(inputData);
  }
}