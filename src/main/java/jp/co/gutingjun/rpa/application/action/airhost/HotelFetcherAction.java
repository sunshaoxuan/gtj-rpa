package jp.co.gutingjun.rpa.application.action.airhost;

import jp.co.gutingjun.rpa.common.RPAConst;
import org.springframework.stereotype.Component;

/** AirHost动作：读取所有房源数据 */
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
  protected Object doAction(Object inputData) {
    return fetchDataList(RPAConst.TAG_HOUSEDATA);
  }
}
