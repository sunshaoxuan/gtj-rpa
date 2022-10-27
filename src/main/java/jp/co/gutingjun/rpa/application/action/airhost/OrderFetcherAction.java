package jp.co.gutingjun.rpa.application.action.airhost;

import jp.co.gutingjun.common.util.DateUtil;
import jp.co.gutingjun.rpa.common.RPAConst;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

@Component
public class OrderFetcherAction extends PagedDataFetcherActionModel {

  /** 过滤起始日期 */
  private LocalDate beginDate;
  /** 过滤截止日期 */
  private LocalDate endDate;

  public OrderFetcherAction() {
    getContext()
        .put(
            RPAConst.URL,
            "https://cloud.airhost.co/ja/bookings.json?"
                + "draw="
                + RPAConst.TAG_REQUESTTIMES
                + "&order[0][dir]=asc&start="
                + RPAConst.TAG_STARTINDEX
                + "&length="
                + RPAConst.TAG_LENGTH
                + "&filter[booking_dates_range]="
                + RPAConst.TAG_FILTER_BEGINDATE
                + "+-+"
                + RPAConst.TAG_FILTER_ENDDATE);

    getContext().put(RPAConst.TAG_PAGESIZE, 20);
    setAccessSleepEnabled(true);
    setMaxSleepSeconds(20);
    appendDependActionClasses(UserPasswordLoginAction.class);
  }

  public LocalDate getBeginDate() {
    return beginDate;
  }

  public void setBeginDate(LocalDate beginDate) {
    this.beginDate = beginDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  @Override
  protected Object beforeDoAction(Object inputData) {
    inputData = super.beforeDoAction(inputData);
    String url = (String) getContext().get(RPAConst.URL);
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    if (getBeginDate() != null && getEndDate() != null) {
      if (getBeginDate().isBefore(getEndDate()) || getBeginDate().isEqual(getEndDate())) {
        url =
            url.replace(
                    RPAConst.TAG_FILTER_BEGINDATE,
                    formatter.format(DateUtil.localDateTodate(getBeginDate())))
                .replace(
                    RPAConst.TAG_FILTER_ENDDATE,
                    formatter.format(DateUtil.localDateTodate(getEndDate())));
        getContext().put(RPAConst.URL, url);
      }
    }
    return inputData;
  }

  @Override
  protected Object doAction() {
    return fetchDataList(RPAConst.TAG_DATA);
  }
}
