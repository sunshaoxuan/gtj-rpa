package jp.co.gutingjun.rpa.application.action.airhost.hotel;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import jp.co.gutingjun.rpa.application.action.airhost.login.UserPasswordLoginAction;
import jp.co.gutingjun.rpa.common.RPAConst;
import jp.co.gutingjun.rpa.model.action.WebClientActionModel;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Map;

public class EditRoomBookableStateAction extends WebClientActionModel {
  /** 房源ID */
  private Long houseId;

  private Long roomTypeId;

  public EditRoomBookableStateAction() {
    getContext()
        .put(
            RPAConst.URL,
            "https://cloud.airhost.co/zh/houses/" + RPAConst.TAG_HOUSEID + "/calendars");
    appendDependActionClasses(UserPasswordLoginAction.class);
  }

  public Long getRoomTypeId() {
    return roomTypeId;
  }

  public void setRoomTypeId(Long roomTypeId) {
    this.roomTypeId = roomTypeId;
  }

  public Long getHouseId() {
    return houseId;
  }

  public void setHouseId(Long houseId) {
    this.houseId = houseId;
  }

  @Override
  protected Object doAction() {
    if (this.getContext().containsKey("HOUSEID")) {
      setHouseId(Long.parseLong((String) this.getContext().get("HOUSEID")));
    }

    if (this.getContext().containsKey("ROOMTYPEID")) {
      setRoomTypeId(Long.parseLong((String) this.getContext().get("ROOMTYPEID")));
    }

    try {
      String url = (String) getContext().get(RPAConst.URL);
      url = url.replace(RPAConst.TAG_HOUSEID, String.valueOf(getHouseId()));
      HtmlPage page = getWebClient().getPage(url);
      HtmlForm form =
          (HtmlForm)
              page.getElementsByTagName("form").stream()
                  .filter(innerForm -> innerForm.getId().startsWith("edit_room_"))
                  .findFirst()
                  .get();
      // 开始日期
      form.getElementsByTagName("input").stream()
          .filter(input -> input.getAttribute("name").equals("dtstart"))
          .findFirst()
          .get()
          .setAttribute("value", ((Map<String, String>) getInputData()).get("dtstart"));
      // 结束日期
      form.getElementsByTagName("input").stream()
          .filter(input -> input.getAttribute("name").equals("dtend"))
          .findFirst()
          .get()
          .setAttribute("value", ((Map<String, String>) getInputData()).get("dtend"));
      // 预定状态
      HtmlSelect select =
          (HtmlSelect)
              form.getElementsByTagName("select").stream()
                  .filter(input -> input.getAttribute("name").equals("status"))
                  .findFirst()
                  .get();
      select.getOptions().stream()
          .filter(
              option ->
                  option
                      .getValueAttribute()
                      .equals(((Map<String, String>) getInputData()).get("status")))
          .findFirst()
          .get()
          .setSelected(true);
      // 预订房间
      select =
          (HtmlSelect)
              form.getElementsByTagName("select").stream()
                  .filter(input -> input.getAttribute("name").equals("room_unit_id"))
                  .findFirst()
                  .get();
      select.getOptions().stream()
          .filter(
              option ->
                  option
                      .getValueAttribute()
                      .equals(
                          (StringUtils.isBlank(
                                  ((Map<String, String>) getInputData()).get("room_unit_id"))
                              ? ""
                              : ((Map<String, String>) getInputData()).get("room_unit_id"))))
          .findFirst()
          .get()
          .setSelected(true);
      form.getElementsByTagName("input").stream()
          .filter(input -> input.getAttribute("type").equals("submit"))
          .findFirst()
          .get()
          .click();
      getWebClient().waitForBackgroundJavaScript(3000);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }

    return true;
  }
}
