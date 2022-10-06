package jp.co.gutingjun.rpa.application.action.airhost;

import jp.co.gutingjun.common.util.DateUtil;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

@Component
public class OrderFetcherAction extends PagedDataFetcherActionModel {
  /** 数据列表标签 */
  final String TAG_DATA = "data";
  /** 过滤起始时间 */
  final String TAG_FILTER_BEGINDATE = "$BEGINDATE$";
  /** 过滤截止时间 */
  final String TAG_FILTER_ENDDATE = "$ENDDATE$";

  /** 过滤起始日期 */
  private LocalDate beginDate;

  public LocalDate getBeginDate() {
    return beginDate;
  }

  public void setBeginDate(LocalDate beginDate) {
    this.beginDate = beginDate;
  }

  /** 过滤截止日期 */
  private LocalDate endDate;

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public OrderFetcherAction() {
    getWebContext()
        .put(
            URL,
            "https://cloud.airhost.co/ja/bookings.json?"
                + "draw="
                + TAG_REQUESTTIMES
                + "&order[0][dir]=asc&start="
                + TAG_STARTINDEX
                + "&length="
                + TAG_LENGTH
                + "&filter[booking_dates_range]="
                + TAG_FILTER_BEGINDATE
                + "+-+"
                + TAG_FILTER_ENDDATE);

    getWebContext().put(TAG_PAGESIZE, 20);
    setAccessSleepEnabled(true);
    setMaxSleepSeconds(20);
    appendDependActionClasses(UserPasswordLoginAction.class);
  }

  @Override
  protected Object beforeDoAction(Object inputData) {
    inputData = super.beforeDoAction(inputData);
    String url = (String) getWebContext().get(URL);
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    if (getBeginDate() != null && getEndDate() != null) {
      if (getBeginDate().isBefore(getEndDate()) || getBeginDate().isEqual(getEndDate())) {
        url =
            url.replace(
                    TAG_FILTER_BEGINDATE,
                    formatter.format(DateUtil.localDateTodate(getBeginDate())))
                .replace(
                    TAG_FILTER_ENDDATE, formatter.format(DateUtil.localDateTodate(getEndDate())));
        getWebContext().put(URL, url);
      }
    }
    return inputData;
  }

  @Override
  protected Object doAction(Object inputData) {
    return fetchDataList(TAG_DATA);
  }
}