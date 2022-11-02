package jp.co.gutingjun.rpa.application.action.airhost.order;

import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebResponse;
import jp.co.gutingjun.rpa.common.RPAConst;
import jp.co.gutingjun.rpa.model.action.WebClientActionModel;
import org.apache.log4j.lf5.util.StreamUtils;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AirHost动作：订单明细页获取订单详细数据动作
 *
 * @author sunsx
 * */
public class OrderDetailFileFetcherAction extends WebClientActionModel {
  private LocalDate beginDate;
  private LocalDate endDate;

  public OrderDetailFileFetcherAction() {
    getContext()
        .put(
            RPAConst.URL,
            "https://cloud.airhost.co/en/bookings.csv?draw="
                + RPAConst.TAG_REQUESTTIMES
                + "&order%5B0%5D%5Bdir%5D=asc&start="
                + RPAConst.TAG_STARTINDEX
                + "&length="
                + RPAConst.TAG_LENGTH
                + "&search%5Bregex%5D=false&filter%5Bdate_type%5D=checkin_date&filter%5Bbooking_dates_range%5D="
                + RPAConst.TAG_BEGINDATE
                + "+-+"
                + RPAConst.TAG_ENDDATE);
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
  protected Object doAction() {
    Map<String, Object> dataMap = new HashMap<>();
    List<String> lineData = new ArrayList<>();
    List<Map<String, Object>> dataList = new ArrayList<>();

    try {
      String url = (String) getContext().get(RPAConst.URL);

      // 先取一条数据，为了取全部房源合计数字
      TextPage page =
          getWebClient()
              .getPage(
                  url.replace(RPAConst.TAG_REQUESTTIMES, "1")
                      .replace(RPAConst.TAG_STARTINDEX, "0")
                      .replace(RPAConst.TAG_LENGTH, "1")
                      .replace(
                          RPAConst.TAG_BEGINDATE,
                          getBeginDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                      .replace(
                          RPAConst.TAG_ENDDATE,
                          getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
      getWebClient().waitForBackgroundJavaScript(30000);
      WebResponse response = page.getWebResponse();
      response.defaultCharsetUtf8();
      InputStream contentAsStream = response.getContentAsStream();
      byte[] content = StreamUtils.getBytes(contentAsStream);
      if (content != null && content.length > 0) {
        String dataCSV = new String(content);
        String[] dataLines = dataCSV.split("\n");
        for (int i = 0; i < dataLines.length; i++) {
          String line = dataLines[i].replace(",\"\",", ",,");
          int length = line.split("\"", -1).length;
          if (Math.ceil(length / 2) * 2 == length) {
            // 偶数为引号截断内容
            String wholeLine = line;
            while (Math.ceil(length / 2) * 2 == length) {
              line = dataLines[++i].replace(",\"\",", ",,");
              wholeLine += line;
              length = wholeLine.split("\"", -1).length;
            }
            lineData.add(wholeLine);
          } else {
            lineData.add(line);
          }
        }
        String[] heads = lineData.get(0).split(",");
        for (int i = 1; i < lineData.size(); i++) {
          Map<String, Object> lineDataMap = new HashMap<>();
          String[] fieldValues = lineData.get(i).split(",");
          if (fieldValues.length > heads.length) {
            String[] lineContents = lineData.get(i).split(",");
            fieldValues = new String[heads.length];
            int index = 0;
            for (int j = 0; j < lineContents.length; j++) {
              String part = lineContents[j];
              if (part.contains("\"")) {
                int length = part.split("\"", -1).length;
                if (Math.ceil(length / 2) * 2 == length) {
                  // 偶数为引号被分拆
                  while (Math.ceil(length / 2) * 2 == length) {
                    part += lineContents[++j];
                    length = part.split("\"", -1).length;
                  }
                }
              }

              fieldValues[index++] = part;
            }
          }

          for (int j = 0; j < heads.length; j++) {
            lineDataMap.put(heads[j], fieldValues[j]);
          }
          dataList.add(lineDataMap);
        }
      }
    } catch (Exception ex) {
    }

    dataMap.put("result", dataList);
    return dataMap;
  }

  @Override
  public void validate() throws Exception {
    if (getBeginDate() == null) {
      throw new RuntimeException("开始日期不能为空。");
    }

    if (getEndDate() == null) {
      throw new RuntimeException("结束日期不能为空。");
    }
  }
}
