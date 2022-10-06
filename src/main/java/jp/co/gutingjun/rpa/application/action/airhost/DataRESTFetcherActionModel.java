package jp.co.gutingjun.rpa.application.action.airhost;

import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import jp.co.gutingjun.common.util.JsonUtils;
import jp.co.gutingjun.rpa.model.action.web.WebClientActionModel;

import java.util.HashMap;
import java.util.Map;

public abstract class DataRESTFetcherActionModel extends WebClientActionModel {
  /** 房源标签 */
  protected final String TAG_HOUSEID = "$HOUSEID$";

  /** 房间类型列表标签 */
  protected final String TAG_ROOMTYPEID = "$ROOMTYPEID$";

  private Long houseId;

  public Long getHouseId() {
    return houseId;
  }

  public void setHouseId(Long houseId) {
    this.houseId = houseId;
  }

  private Long roomTypeId;

  public Long getRoomTypeId() {
    return roomTypeId;
  }

  public void setRoomTypeId(Long roomTypeId) {
    this.roomTypeId = roomTypeId;
  }

  protected Map<String, Object> fetchData() {
    Map<String, Object> dataMap = new HashMap<>();

    try {
      String url =
          ((String) getWebContext().get(URL))
              .replace(TAG_HOUSEID, String.valueOf(getHouseId()))
              .replace(TAG_ROOMTYPEID, String.valueOf(getRoomTypeId()));
      Object page = getWebClient().getPage(url);

      WebResponse response = null;
      if (page instanceof UnexpectedPage) {
        response = ((UnexpectedPage) page).getWebResponse();
        if (response.getContentType().equals("application/json")) {
          String json = response.getContentAsString();
          dataMap = JsonUtils.json2Map(json);
        }
      }else if(page instanceof HtmlPage){
        ((HtmlPage) page).getElementsByTagName("a");
        String redirectUrl = ((HtmlPage) page).getElementsByTagName("a").stream().findFirst().get().getAttribute("href");
        this.setURL(redirectUrl + ".json");
        dataMap = fetchData();
      }
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }

    return dataMap;
  }
}