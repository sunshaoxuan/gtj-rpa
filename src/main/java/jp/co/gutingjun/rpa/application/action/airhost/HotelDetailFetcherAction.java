package jp.co.gutingjun.rpa.application.action.airhost;

import jp.co.gutingjun.rpa.common.RPAConst;

import java.util.HashMap;
import java.util.Map;

public class HotelDetailFetcherAction extends DataRESTFetcherActionModel {
  public HotelDetailFetcherAction() {
    getContext()
        .put(RPAConst.URL, "https://cloud.airhost.co/en/houses/" + RPAConst.TAG_HOUSEID + ".json");
    appendDependActionClasses(UserPasswordLoginAction.class);
  }

  @Override
  protected Object doAction() {
    Map<String, Object> result = new HashMap<>();
    Map dataMap = fetchData();
    result.put(String.valueOf(getHouseId()), dataMap);
    setOutputData(result);
    return result != null && result.size() > 0;
  }
}
