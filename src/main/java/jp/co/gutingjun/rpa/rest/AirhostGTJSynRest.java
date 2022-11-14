package jp.co.gutingjun.rpa.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jp.co.gutingjun.common.util.FileUtil;
import jp.co.gutingjun.common.util.JsonUtils;
import jp.co.gutingjun.common.util.R;
import jp.co.gutingjun.rpa.common.TemplateConst;
import jp.co.gutingjun.rpa.rest.dto.HotelInfoDTO;
import jp.co.gutingjun.rpa.rest.dto.OrderRangeDTO;
import jp.co.gutingjun.rpa.rest.dto.RoomStatusDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.*;

import static com.xiaoleilu.hutool.util.ClassLoaderUtil.getClassLoader;

/**
 * Airhost-谷町群旅馆同步服务
 *
 * @author sunsx
 */
@RestController
@Slf4j
@Api(
    value = "rpa rest",
    tags = {"rpa"})
@RequestMapping("rpa")
public class AirhostGTJSynRest {
  @ApiOperation(value = "下载房源资料", notes = "下载房源资料")
  @PostMapping(value = "/readhotel")
  @ResponseBody
  public R readHotel(@RequestBody HotelInfoDTO hotelInfoDTO) {
    String rpaTemplate = loadTemplate(TemplateConst.RPATEMPLATE_ONCE_HOTEL_READ);
    rpaTemplate = replaceLoginInfo(rpaTemplate);
    rpaTemplate =
        rpaTemplate.replace("$AIRHOST_HOTEL_ID$", String.valueOf(hotelInfoDTO.getHotelId()));
    return new BotRest().regAndRunBot(rpaTemplate);
  }

  @ApiOperation(value = "更新AirHost房源资料", notes = "更新AirHost房源资料")
  @PostMapping(value = "/edithotel")
  @ResponseBody
  public R editHotel(@RequestBody HotelInfoDTO hotelInfoDTO) {
    String rpaTemplate = loadTemplate(TemplateConst.RPATEMPLATE_ONCE_HOTEL_UPDATE);
    rpaTemplate = replaceLoginInfo(rpaTemplate);
    rpaTemplate =
        rpaTemplate.replace("$AIRHOST_HOUSEID$", String.valueOf(hotelInfoDTO.getHotelId()));
    Map<String, Object> botSettings = JsonUtils.json2Map(rpaTemplate);
    if (botSettings.containsKey("bot")) {
      if (((Map<String, Object>) botSettings.get("bot")).containsKey("jobs")) {
        Map<String, Object> editJob =
            ((List<Map<String, Object>>) ((Map<String, Object>) botSettings.get("bot")).get("jobs"))
                .stream()
                    .filter(job -> job.get("type").equals("EditRoomBookableStateAction"))
                    .findFirst()
                    .get();
        if (editJob != null) {
          editJob.put("inputdata", hotelInfoDTO.getValueMap());
        }
      }
    }
    return new BotRest()
        .regAndRunBot(
            JsonUtils.map2JSON(new ArrayList<Map<String, Object>>(Arrays.asList(botSettings))));
  }

  @ApiOperation(value = "单次下载订单资料", notes = "单次下载订单资料")
  @PostMapping(value = "/readorders")
  @ResponseBody
  public R readOrders(@RequestBody OrderRangeDTO orderRangeDTO) {
    String rpaTemplate = loadTemplate(TemplateConst.RPATEMPLATE_ONCE_ORDER_READ);
    rpaTemplate = replaceLoginInfo(rpaTemplate);
    Map<String, Object> botSettings = JsonUtils.json2Map(rpaTemplate);
    if (botSettings.containsKey("bot")) {
      if (((Map<String, Object>) botSettings.get("bot")).containsKey("jobs")) {
        Map<String, Object> editJob =
            ((List<Map<String, Object>>) ((Map<String, Object>) botSettings.get("bot")).get("jobs"))
                .stream()
                    .filter(job -> job.get("type").equals("OrderFetcherAction"))
                    .findFirst()
                    .get();
        if (editJob != null) {
          Map<String, String> valueMap = new HashMap<>();
          valueMap.put("beginDate", orderRangeDTO.getBeginDate());
          valueMap.put("endDate", orderRangeDTO.getEndDate());
          editJob.put("inputdata", valueMap);
        }
      }
    }
    return new BotRest()
        .regAndRunBot(
            JsonUtils.map2JSON(new ArrayList<Map<String, Object>>(Arrays.asList(botSettings))));
  }

  @ApiOperation(value = "设置房间状态", notes = "设置房间状态")
  @PostMapping(value = "/setroomstatus")
  @ResponseBody
  public R setRoomStatus(@RequestBody RoomStatusDTO roomStatusDTO) {
    try {
      if (roomStatusDTO.getHotelId() <= 0) {
        throw new Exception("Hotel ID can not be empty.");
      }

      if (roomStatusDTO.getRoomTypeId() <= 0) {
        throw new Exception("Room Type ID can not be empty.");
      }

      String rpaTemplate = loadTemplate(TemplateConst.RPATEMPLATE_ONCE_ROOM_STATUS_UDPATE);
      rpaTemplate = replaceLoginInfo(rpaTemplate);
      rpaTemplate =
          rpaTemplate
              .replace("$AIRHOST_HOUSEID$", String.valueOf(roomStatusDTO.getHotelId()))
              .replace("$AIRHOST_ROOMTYPEID$", String.valueOf(roomStatusDTO.getRoomTypeId()));
      Map<String, Object> botSettings = JsonUtils.json2Map(rpaTemplate);
      if (botSettings.containsKey("bot")) {
        if (((Map<String, Object>) botSettings.get("bot")).containsKey("jobs")) {
          Map<String, Object> editJob =
              ((List<Map<String, Object>>)
                      ((Map<String, Object>) botSettings.get("bot")).get("jobs"))
                  .stream()
                      .filter(job -> job.get("type").equals("EditRoomBookableStateAction"))
                      .findFirst()
                      .get();
          if (editJob != null) {
            Map<String, Object> inputData = new HashMap<>();
            inputData.put("dtstart", roomStatusDTO.getBeginDate());
            inputData.put("dtend", roomStatusDTO.getEndDate());
            inputData.put("status", roomStatusDTO.getStatus());
            inputData.put("room_unit_id", roomStatusDTO.getRoomId());
            editJob.put("inputdata", inputData);
          }
        }
      }
      return new BotRest()
          .regAndRunBot(
              JsonUtils.map2JSON(new ArrayList<Map<String, Object>>(Arrays.asList(botSettings))));
    } catch (Exception ex) {
      return R.responseByError(500, ex.getMessage());
    }
  }

  private String replaceLoginInfo(String template) {
    return template
        .replace("$AIRHOST_MANAGE_USERNAME$", TemplateConst.AIRHOST_MANAGE_USERNAME)
        .replace("$AIRHOST_MANAGE_PASSWORD$", TemplateConst.AIRHOST_MANAGE_PASSWORD)
        .replace("$GTJ_MANAGE_USERNAME$", TemplateConst.GTJ_MANAGE_USERNAME)
        .replace("$GTJ_MANAGE_PASSCODE$", TemplateConst.GTJ_MANAGE_PASSCODE);
  }

  private String loadTemplate(String templateName) {
    URL url = getClassLoader().getResource("json/" + templateName);
    try {
      String fileContent = FileUtil.readFileAsString(url.getPath());
      return fileContent.replace("\r\n", "");
    } catch (Exception ex) {
      throw new RuntimeException(ex.getMessage());
    }
  }
}
