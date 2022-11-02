package jp.co.gutingjun.rpa.application.action.airhost.order;

import jp.co.gutingjun.rpa.application.action.airhost.model.DocDetailFORMFetcherActionModel;
import jp.co.gutingjun.rpa.common.RPAConst;
import org.springframework.stereotype.Component;

/**
 * AirHost动作：表单获取订单数据动作
 *
 * @author sunsx
 * */
@Component
public class OrderDetailFetcherAction extends DocDetailFORMFetcherActionModel {
  public OrderDetailFetcherAction() {
    getContext()
        .put(
            RPAConst.URL, "https://cloud.airhost.co/en/bookings/" + RPAConst.TAG_ORDERID + "/edit");
  }

  @Override
  protected Object doAction() {
    setFormId("edit_booking_" + getOrderId());
    return fetchDataMap();
  }
}
