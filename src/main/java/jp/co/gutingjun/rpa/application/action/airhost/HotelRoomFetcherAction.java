package jp.co.gutingjun.rpa.application.action.airhost;

import jp.co.gutingjun.rpa.common.RPAConst;

import java.util.HashMap;
import java.util.Map;

public class HotelRoomFetcherAction extends DataRESTFetcherActionModel {
  public HotelRoomFetcherAction() {
    getContext()
        .put(
            RPAConst.URL,
            "https://cloud.airhost.co/en/houses/" + RPAConst.TAG_HOUSEID + "/room_units.json");
    appendDependActionClasses(UserPasswordLoginAction.class);
  }

  @Override
  protected Object doAction(Object inputData) {
    Map<String, Object> result = new HashMap<>();
    Map dataMap = fetchData();
    result.put(String.valueOf(getHouseId()), dataMap);
    return dataMap;
  }
}
