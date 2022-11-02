package jp.co.gutingjun.rpa.application.action.airhost.hotel;

import jp.co.gutingjun.rpa.application.action.airhost.login.UserPasswordLoginAction;
import jp.co.gutingjun.rpa.application.action.airhost.model.PagedDataFetcherActionModel;
import jp.co.gutingjun.rpa.common.RPAConst;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * AirHost动作：读取所有房源数据
 *
 * @author sunsx
 * */
@Component
public class HotelFetcherAction extends PagedDataFetcherActionModel {
  public HotelFetcherAction() {
    getContext()
        .put(
            RPAConst.URL,
            "https://cloud.airhost.co/ja/houses.json?"
                + "draw="
                + RPAConst.TAG_REQUESTTIMES
                + "&start="
                + RPAConst.TAG_STARTINDEX
                + "&length="
                + RPAConst.TAG_LENGTH);

    getContext().put(RPAConst.TAG_PAGESIZE, 5);
    setAccessSleepEnabled(true);
    setMaxSleepSeconds(5);
    appendDependActionClasses(UserPasswordLoginAction.class);
  }

  @Override
  protected Object doAction() {
    Map result = fetchDataList(RPAConst.TAG_HOUSEDATA);
    setOutputData(result);
    return result != null && result.size() > 0 && result.containsKey("result");
  }
}
