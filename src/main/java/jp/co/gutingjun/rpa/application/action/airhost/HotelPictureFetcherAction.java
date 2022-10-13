package jp.co.gutingjun.rpa.application.action.airhost;

import jp.co.gutingjun.rpa.common.RPAConst;

public class HotelPictureFetcherAction extends DataRESTFetcherActionModel {
  public HotelPictureFetcherAction() {
    getContext()
        .put(
            RPAConst.URL,
            "https://cloud.airhost.co/en/houses/" + RPAConst.TAG_HOUSEID + "/photos.json");
    appendDependActionClasses(UserPasswordLoginAction.class);
  }

  @Override
  protected Object doAction(Object inputData) {
    return fetchData();
  }
}
