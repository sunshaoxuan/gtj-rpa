package jp.co.gutingjun.rpa.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jp.co.gutingjun.common.util.R;
import jp.co.gutingjun.rpa.application.BotBus;
import jp.co.gutingjun.rpa.inf.IUserService;
import jp.co.gutingjun.rpa.model.bot.BotInstance;
import jp.co.gutingjun.rpa.model.bot.BotModel;
import jp.co.gutingjun.rpa.model.bot.IBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Slf4j
@Api(
    value = "rpa rest",
    tags = {"rpa"})
@RequestMapping("rpa")
public class BotRest {
  @Autowired IUserService userService;

  @ApiOperation(value = "RPA服务状态", notes = "RPA服务状态")
  @PostMapping(value = "/service")
  @ResponseBody
  public R RPAServiceStatu() {
    return R.responseBySuccess("RPA service is alive.");
  }

  @ApiOperation(value = "RPA机器人注册", notes = "RPA机器人注册")
  @PostMapping(value = "/botreg")
  @ResponseBody
  public R botRegister(@RequestBody String botContext) {
    try {
      IBot bot = BotBus.botRegister(botContext);
      return R.responseBySuccess(
          "RPA bot [InstanceID:"
              + ((BotInstance) bot).getInstanceId()
              + "], [BotID:"
              + bot.getId()
              + "] registered.");
    } catch (Exception e) {
      return R.responseByError(500, e.getMessage());
    }
  }

  @ApiOperation(value = "已注册机器人列表", notes = "已注册机器人列表")
  @PostMapping(value = "/waitbotlist")
  @ResponseBody
  public R<List<Map<String, String>>> botList() {
    List<Map<String, String>> botCandidates = new ArrayList<Map<String, String>>();
    HashSet<BotModel> candidateSet = BotBus.getCandidateSet();
    candidateSet.forEach(
        candidate -> {
          Map<String, String> newBot = new HashMap<>();
          newBot.put("name", candidate.getName());
          newBot.put("desc", candidate.getDescription());
          newBot.put("id", String.valueOf(candidate.getId()));
          botCandidates.add(newBot);
        });
    return R.responseBySuccess(botCandidates);
  }

  @ApiOperation(value = "执行中机器人列表", notes = "执行中机器人列表")
  @PostMapping(value = "/runningbotlist")
  @ResponseBody
  public R<List<Map<String, String>>> runningBotList() {
    List<Map<String, String>> botRunning = new ArrayList<Map<String, String>>();
    HashSet<BotInstance> runningSet = BotBus.getExecutingBot();
    runningSet.forEach(
        botInstance -> {
          Map<String, String> newBot = new HashMap<>();
          newBot.put("name", botInstance.getName());
          newBot.put("desc", botInstance.getDescription());
          newBot.put("id", String.valueOf(botInstance.getId()));
          newBot.put("instanceId", String.valueOf(botInstance.getInstanceId()));
          botRunning.add(newBot);
        });
    return R.responseBySuccess(botRunning);
  }

  @ApiOperation(value = "机器人手工启动", notes = "机器人手工启动")
  @PostMapping(value = "/botmanualstart")
  @ResponseBody
  public R botStart(@RequestBody Long botID) {
    BotBus.manualExecuteBot(botID);
    return R.responseBySuccess();
  }

  @ApiOperation(value = "机器人平台创建用户", notes = "机器人平台创建用户")
  @PostMapping(value = "/createuser")
  @ResponseBody
  public R createUser(
      @RequestBody UserDTO userObj) {
    try {
      userService.createUser(userObj.getUsername(), userObj.getEmail(), userObj.getPassword(), userObj.isAdmin());
      return R.responseBySuccess();
    } catch (Exception e) {
      return R.responseByError(500, e.getMessage());
    }
  }

  @ApiOperation(value = "机器人平台修改用户", notes = "机器人平台修改用户")
  @PostMapping(value = "/modifyuser")
  @ResponseBody
  public R modifyUser(@RequestBody UserDTO userObj){
    try {
      userService.modifiyUser(userObj.getUsername(), userObj.getEmail(), userObj.getPassword());
      return R.responseBySuccess();
    } catch (Exception e) {
      return R.responseByError(500, e.getMessage());
    }
  }


  @ApiOperation(value = "机器人平台删除用户", notes = "机器人平台删除用户")
  @PostMapping(value = "/removeuser")
  @ResponseBody
  public R removeUser(@RequestBody String username){
    try {
      userService.removeUser(username);
      return R.responseBySuccess();
    } catch (Exception e) {
      return R.responseByError(500, e.getMessage());
    }
  }

  @ApiOperation(value = "机器人平台用户验证邮箱", notes = "机器人平台用户验证邮箱")
  @PostMapping(value = "/verifyemail")
  @ResponseBody
  public R verifyEmail(@RequestBody UserDTO userObj){
    try {
      userService.verityEmail(userObj.getUsername(),userObj.getVerifycode());
      return R.responseBySuccess();
    } catch (Exception e) {
      return R.responseByError(500, e.getMessage());
    }
  }

  @ApiOperation(value = "机器人平台登录", notes = "机器人平台登录")
  @PostMapping(value = "/login")
  @ResponseBody
  public R login(@RequestBody UserDTO userObj) {
    try {
      HashMap token = new HashMap();
      token.put("token", userService.login(userObj.getUsername(), userObj.getPassword()));
      return R.responseBySuccess(token);
    } catch (Exception e) {
      return R.responseByError(500, e.getMessage());
    }
  }
}