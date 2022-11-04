package jp.co.gutingjun.rpa.application.action.airhost.hotel;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import jp.co.gutingjun.rpa.application.action.airhost.login.UserPasswordLoginAction;
import jp.co.gutingjun.rpa.common.RPAConst;
import jp.co.gutingjun.rpa.model.action.WebClientActionModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditHotelInfoAction extends WebClientActionModel {
  /** 房源ID */
  private Long houseId;

  public EditHotelInfoAction() {
    getContext().put(RPAConst.URL, "https://cloud.airhost.co/en/houses/" + RPAConst.TAG_HOUSEID);
    appendDependActionClasses(UserPasswordLoginAction.class);
  }

  public Long getHouseId() {
    return houseId;
  }

  public void setHouseId(Long houseId) {
    this.houseId = houseId;
  }

  @Override
  protected Object doAction() {
    if (this.getContext().containsKey("HOUSEID")) {
      setHouseId(Long.parseLong((String) this.getContext().get("HOUSEID")));
    }
    try {
      String url = (String) getContext().get(RPAConst.URL);
      url = url.replace(RPAConst.TAG_HOUSEID, String.valueOf(getHouseId()));
      WebRequest request = new WebRequest(new java.net.URL(url), HttpMethod.POST);
      List<NameValuePair> data = new ArrayList<>();
      Map<String, String> inputData = (Map<String, String>) getInputData();
      inputData.forEach(
          (key, value) -> {
            data.add(new NameValuePair(key, value));
          });
      request.setRequestParameters(data);
      getWebClient().getPage(request);
      getWebClient().waitForBackgroundJavaScript(3000);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }

    return getOutputData();
  }
}
