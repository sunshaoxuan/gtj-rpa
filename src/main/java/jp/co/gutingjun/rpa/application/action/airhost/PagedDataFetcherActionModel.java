package jp.co.gutingjun.rpa.application.action.airhost;

import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebResponse;
import jp.co.gutingjun.common.util.JsonUtils;
import jp.co.gutingjun.common.util.PageUtil;
import jp.co.gutingjun.rpa.common.CommonUtils;
import jp.co.gutingjun.rpa.model.action.web.WebClientActionModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PagedDataFetcherActionModel extends WebClientActionModel {
  /** 房源标签 */
  protected final String TAG_HOUSEID = "$HOUSEID$";
  /** 查询起始记录数 */
  protected final String TAG_STARTINDEX = "$STARTINDEX$";
  /** 最大记录集长度标签 */
  protected final String TAG_LENGTH = "$LENGTH$";
  /** 请求次数标签 */
  protected final String TAG_REQUESTTIMES = "$REQUESTTIMES$";
  /** 总记录数标签 */
  protected final String TAG_TOTALRECORDS = "recordsFiltered";
  /** 记录分页大小标签 */
  protected final String TAG_PAGESIZE = "pageSize";

  private Long houseId;
  /** 是否启用访问休眠机制，以避免发生高频访问 */
  private boolean accessSleepEnabled = false;
  /** 启用访问休眠机制时，最大休眠秒数</br> （休眠时间随机生成，不超过最大休眠秒数） */
  private int maxSleepSeconds = 1;

  public Long getHouseId() {
    return houseId;
  }

  public void setHouseId(Long houseId) {
    this.houseId = houseId;
  }

  public boolean isAccessSleepEnabled() {
    return accessSleepEnabled;
  }

  public void setAccessSleepEnabled(boolean accessSleepEnabled) {
    this.accessSleepEnabled = accessSleepEnabled;
  }

  public int getMaxSleepSeconds() {
    if (maxSleepSeconds < 1) {
      maxSleepSeconds = 1;
    }

    return maxSleepSeconds;
  }

  public void setMaxSleepSeconds(int maxSleepSeconds) {
    this.maxSleepSeconds = maxSleepSeconds;
  }

  protected Map<String, Object> fetchDataList(String tagDataList) {
    Map<String, Object> dataMap = new HashMap<>();
    List dataList = new ArrayList();

    try {
      String url = (String) getWebContext().get(URL);
      // 先取一条数据，为了取全部房源合计数字
      UnexpectedPage page =
          getWebClient()
              .getPage(
                  url.replace(TAG_HOUSEID, String.valueOf(getHouseId()))
                      .replace(TAG_REQUESTTIMES, "1")
                      .replace(TAG_STARTINDEX, "0")
                      .replace(TAG_LENGTH, "1"));
      WebResponse response = page.getWebResponse();
      if (response.getContentType().equals("application/json")) {
        String json = response.getContentAsString();
        Map<String, Object> result = JsonUtils.json2Map(json);
        if (result.containsKey(TAG_TOTALRECORDS)) {
          int pageCount =
              PageUtil.totalPage(
                  Integer.parseInt(String.valueOf(result.get(TAG_TOTALRECORDS))),
                  (Integer) getWebContext().get(TAG_PAGESIZE));
          for (int i = 0; i < pageCount; i++) {
            page =
                getWebClient()
                    .getPage(
                        url.replace(TAG_HOUSEID, String.valueOf(getHouseId()))
                            .replace(TAG_REQUESTTIMES, String.valueOf(i + 2))
                            .replace(
                                TAG_STARTINDEX,
                                String.valueOf(i * (Integer) getWebContext().get(TAG_PAGESIZE)))
                            .replace(
                                TAG_LENGTH, String.valueOf(getWebContext().get(TAG_PAGESIZE))));
            if (response.getContentType().equals("application/json")) {
              response = page.getWebResponse();
              json = response.getContentAsString();
              result = JsonUtils.json2Map(json);
              if (result.containsKey(tagDataList)) {
                ((List<Map<String, Object>>) result.get(tagDataList))
                    .forEach(
                        map -> {
                          if (getHouseId() == null
                              || getHouseId() <= 0
                              || !map.containsKey("id")
                              || String.valueOf(getHouseId()).equals(map.get("id"))) {
                            dataList.add(map);
                          }
                        });
              }

              if (pageCount > 1 && isAccessSleepEnabled()) {
                // 线程休眠，以确保不进行高频访问
                // 最少休眠1秒
                CommonUtils.sleepRandomSeconds(getMaxSleepSeconds());
              }
            }
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }

    dataMap.put("result", dataList);
    return dataMap;
  }
}
