package jp.co.gutingjun.rpa.application.action.airhost;

import java.util.HashMap;
import java.util.Map;

public class HotelRoomFetcherAction extends DataRESTFetcherActionModel {
  public HotelRoomFetcherAction() {
    getWebContext()
        .put(URL, "https://cloud.airhost.co/en/houses/" + TAG_HOUSEID + "/room_units.json");
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