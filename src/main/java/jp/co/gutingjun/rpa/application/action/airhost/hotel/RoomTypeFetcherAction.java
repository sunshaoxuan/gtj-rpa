package jp.co.gutingjun.rpa.application.action.airhost.hotel;

import jp.co.gutingjun.rpa.application.action.airhost.login.UserPasswordLoginAction;
import jp.co.gutingjun.rpa.application.action.airhost.model.DataRESTFetcherActionModel;
import jp.co.gutingjun.rpa.common.RPAConst;

import java.util.HashMap;
import java.util.Map;

/**
 * AirHost动作：房间类型获取动作
 *
 * @author sunsx
 */
public class RoomTypeFetcherAction extends DataRESTFetcherActionModel {
  public RoomTypeFetcherAction() {
    getContext()
        .put(
            RPAConst.URL,
            "https://cloud.airhost.co/en/houses/" + RPAConst.TAG_HOUSEID + "/rooms.json");
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
