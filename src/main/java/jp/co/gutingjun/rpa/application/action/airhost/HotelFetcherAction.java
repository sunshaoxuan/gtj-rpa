package jp.co.gutingjun.rpa.application.action.airhost;

import org.springframework.stereotype.Component;

/** AirHost动作：读取所有房源数据 */
@Component
public class HotelFetcherAction extends PagedDataFetcherActionModel {
  /** 房源列表标签 */
  final String TAG_DATA = "houses";

  public HotelFetcherAction() {
    getWebContext()
        .put(
            URL,
            "https://cloud.airhost.co/ja/houses.json?"
                + "draw="
                + TAG_REQUESTTIMES
                + "&start="
                + TAG_STARTINDEX
                + "&length="
                + TAG_LENGTH);

    getWebContext().put(TAG_PAGESIZE, 5);
    setAccessSleepEnabled(true);
    setMaxSleepSeconds(5);
    appendDependActionClasses(UserPasswordLoginAction.class);
  }

  @Override
  protected Object doAction(Object inputData) {
    return fetchDataList(TAG_DATA);
  }
}