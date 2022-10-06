package jp.co.gutingjun.rpa.application.action.airhost;

import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebResponse;
import jp.co.gutingjun.rpa.model.action.web.WebClientActionModel;
import org.apache.log4j.lf5.util.StreamUtils;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDetailFileFetcherAction extends WebClientActionModel {
  /** 请求次数标签 */
  protected final String TAG_REQUESTTIMES = "$REQUESTTIMES$";
  /** 查询起始记录数 */
  protected final String TAG_STARTINDEX = "$STARTINDEX$";
  /** 最大记录集长度标签 */
  protected final String TAG_LENGTH = "$LENGTH$";
  /** 开始日期 */
  protected final String TAG_BEGINDATE = "$BEGINDATE$";
  /** 结束日期 */
  protected final String TAG_ENDDATE = "$ENDDATE$";

  private LocalDate beginDate;
  private LocalDate endDate;

  public OrderDetailFileFetcherAction() {
    getWebContext()
        .put(
            URL,
            "https://cloud.airhost.co/en/bookings.csv?draw="
                + TAG_REQUESTTIMES
                + "&order%5B0%5D%5Bdir%5D=asc&start="
                + TAG_STARTINDEX
                + "&length="
                + TAG_LENGTH
                + "&search%5Bregex%5D=false&filter%5Bdate_type%5D=checkin_date&filter%5Bbooking_dates_range%5D="
                + TAG_BEGINDATE
                + "+-+"
                + TAG_ENDDATE);
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
  protected Object doAction(Object inputData) {
    Map<String, Object> dataMap = new HashMap<>();
    List<String> lineData = new ArrayList<>();
    List<Map<String, Object>> dataList = new ArrayList<>();

    try {
      String url = (String) getWebContext().get(URL);

      // 先取一条数据，为了取全部房源合计数字
      TextPage page =
          getWebClient()
              .getPage(
                  url.replace(TAG_REQUESTTIMES, "1")
                      .replace(TAG_STARTINDEX, "0")
                      .replace(TAG_LENGTH, "1")
                      .replace(
                          TAG_BEGINDATE,
                          getBeginDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                      .replace(
                          TAG_ENDDATE,
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
  public void validate(Object inputData) throws Exception {
    if (getBeginDate() == null) {
      throw new RuntimeException("开始日期不能为空。");
    }

    if (getEndDate() == null) {
      throw new RuntimeException("结束日期不能为空。");
    }
  }
}