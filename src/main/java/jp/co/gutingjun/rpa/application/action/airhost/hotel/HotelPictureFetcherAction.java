package jp.co.gutingjun.rpa.application.action.airhost.hotel;

import jp.co.gutingjun.rpa.application.action.airhost.login.UserPasswordLoginAction;
import jp.co.gutingjun.rpa.application.action.airhost.model.DataRESTFetcherActionModel;
import jp.co.gutingjun.rpa.common.RPAConst;

import java.util.Map;

/**
 * AirHost动作：房源图片获取动作
 *
 * @author sunsx
 * */
public class HotelPictureFetcherAction extends DataRESTFetcherActionModel {
  public HotelPictureFetcherAction() {
    getContext()
        .put(
            RPAConst.URL,
            "https://cloud.airhost.co/en/houses/" + RPAConst.TAG_HOUSEID + "/photos.json");
    appendDependActionClasses(UserPasswordLoginAction.class);
  }

  @Override
  protected Object doAction() {
    Map<String, Object> result = fetchData();
    setOutputData(result);
    return result != null && result.size() > 0;
  }
}
