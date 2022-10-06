package jp.co.gutingjun.rpa.application.action.airhost;

import org.springframework.stereotype.Component;

@Component
public class OrderDetailFetcherAction extends DocDetailFORMFetcherActionModel {
  public OrderDetailFetcherAction() {
    getWebContext().put(URL, "https://cloud.airhost.co/en/bookings/" + TAG_ORDERID + "/edit");
  }

  @Override
  protected Object doAction(Object inputData) {
    setFormId("edit_booking_" + getOrderId());
    return fetchDataMap();
  }
}