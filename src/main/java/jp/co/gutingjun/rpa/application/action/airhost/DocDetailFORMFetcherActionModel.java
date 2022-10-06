package jp.co.gutingjun.rpa.application.action.airhost;

import com.gargoylesoftware.htmlunit.html.*;
import jp.co.gutingjun.rpa.common.CommonUtils;
import jp.co.gutingjun.rpa.model.action.web.WebClientActionModel;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class DocDetailFORMFetcherActionModel extends WebClientActionModel {
  final String TAG_HOUSEID = "$HOUSEID$";

  final String TAG_ROOMID = "$ROOMID$";

  final String TAG_ROOMTYPEID = "$ROOMTYPEID$";

  final String TAG_ORDERID = "$ORDERID$";

  /** 房源ID */
  private Long houseId;

  public Long getHouseId() {
    return houseId;
  }

  public void setHouseId(Long houseId) {
    this.houseId = houseId;
  }

  /** 房间ID */
  private Long roomId;

  public Long getRoomId() {
    return roomId;
  }

  public void setRoomId(Long roomId) {
    this.roomId = roomId;
  }

  private Long roomTypeId;

  public Long getRoomTypeId() {
    return roomTypeId;
  }

  public void setRoomTypeId(Long roomTypeId) {
    this.roomTypeId = roomTypeId;
  }

  /** 订单ID */
  private Long orderId;

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  private String formId;

  public String getFormId() {
    return formId;
  }

  public void setFormId(String formId) {
    this.formId = formId;
  }

  protected Map fetchDataMap() {
    Map<String, String> fetchedDataMap = new HashMap<>();
    try {
      String url = (String) getWebContext().get(URL);
      url =
          url.replace(TAG_HOUSEID, String.valueOf(getHouseId()))
              .replace(TAG_ROOMID, String.valueOf(getRoomId()))
                  .replace(TAG_ROOMTYPEID, String.valueOf(getRoomTypeId()))
              .replace(TAG_ORDERID, String.valueOf(getOrderId()));
      HtmlPage page = getWebClient().getPage(url);
      getWebClient().waitForBackgroundJavaScript(10000);
      HtmlForm form =
          (HtmlForm)
              page.getElementsByTagName("form").stream()
                  .filter(roomForm -> roomForm.getId().equals(getFormId()))
                  .findFirst()
                  .get();
      form.getElementsByTagName("input")
          .forEach(
              input -> {
                String name = input.getAttribute("name");
                String value = input.getAttribute("value");
                String text = input.getTextContent();

                if (!StringUtils.isBlank(name)
                    && !name.startsWith("_")
                    && !name.startsWith("utf8")) {
                  if (input instanceof HtmlCheckBoxInput) {
                    fetchedDataMap.put(
                        name.replace("[]", "[" + value + "]"), input.getAttribute("checked"));
                  } else if (input instanceof HtmlTextInput || input instanceof HtmlHiddenInput) {
                    fetchedDataMap.put(name, value);
                  } else {
                    fetchedDataMap.put(name, text);
                  }
                }
              });

      form.getElementsByTagName("textarea")
          .forEach(
              textarea -> {
                String name = textarea.getAttribute("name");
                String text = textarea.getTextContent();

                if (!StringUtils.isBlank(name) && name.startsWith("room")) {
                  fetchedDataMap.put(name, text);
                }
              });

      return getFetchedDataMap(fetchedDataMap);
    } catch (Exception ex) {
      throw new RuntimeException(ex.getMessage());
    }
  }

  protected Map getFetchedDataMap(Map<String, String> roomData) {
    Map fetchedMap = new HashMap();
    roomData.forEach(
        (key, value) -> {
          String[] keys = key.split("\\[");
          Arrays.setAll(keys, i -> keys[i].replace("]", ""));
          Map result = CommonUtils.getDataMap(keys, value);
          CommonUtils.mapPutAll(fetchedMap, result);
        });

    return fetchedMap;
  }
}