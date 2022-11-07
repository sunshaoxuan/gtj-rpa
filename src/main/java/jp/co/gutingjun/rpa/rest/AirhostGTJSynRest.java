package jp.co.gutingjun.rpa.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jp.co.gutingjun.common.util.FileUtil;
import jp.co.gutingjun.common.util.JsonUtils;
import jp.co.gutingjun.common.util.R;
import jp.co.gutingjun.rpa.common.TemplateConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
  public R readHotel() {
    return R.responseBySuccess("Hotel read.");
  }

  @ApiOperation(value = "同步房源资料", notes = "同步房源资料")
  @PostMapping(value = "/edithotel")
  @ResponseBody
  public R editHotel() {
    return R.responseBySuccess("Hotel edited.");
  }

  @ApiOperation(value = "下载订单资料", notes = "下载订单资料")
  @PostMapping(value = "/readorders")
  @ResponseBody
  public R readOrders() {
    return R.responseBySuccess("Order read.");
  }

  @ApiOperation(value = "设置房间状态", notes = "设置房间状态")
  @PostMapping(value = "/setroomstatus")
  @ResponseBody
  public R setRoomStatus(@RequestBody RoomStatusDTO roomStatusDTO) {
    String rpaTemplate = loadTemplate(TemplateConst.RPATEMPLATE_ONCE_ROOM_STATUS_UDPATE);
    rpaTemplate = replaceLoginInfo(rpaTemplate);
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
          Map<String, Object> inputData = new HashMap<>();
          inputData.put("dtstart", roomStatusDTO.getBeginDate());
          inputData.put("dtend", roomStatusDTO.getEndDate());
          inputData.put("status", roomStatusDTO.getStatus());
          inputData.put("room_unit_id", roomStatusDTO.getRoomId());
          inputData.put("utf8", "✓");
          editJob.put("inputdata", inputData);
        }
      }
    }
    return R.responseBySuccess("Room status set.");
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
