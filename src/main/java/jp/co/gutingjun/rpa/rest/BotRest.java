package jp.co.gutingjun.rpa.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.undertow.util.DateUtils;
import jp.co.gutingjun.common.pms.TreeNode;
import jp.co.gutingjun.common.util.R;
import jp.co.gutingjun.rpa.application.BotBus;
import jp.co.gutingjun.rpa.inf.IUserService;
import jp.co.gutingjun.rpa.model.bot.BotInstance;
import jp.co.gutingjun.rpa.model.bot.BotModel;
import jp.co.gutingjun.rpa.model.bot.IBot;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
      if (bot == null) {
        return R.responseByError(200, "Bot exists.");
      } else {
        return R.responseBySuccess("RPA bot [BotID:" + bot.getId() + "] registered.");
      }
    } catch (Exception e) {
      return R.responseByError(500, e.getMessage());
    }
  }

  @ApiOperation(value = "已注册机器人列表", notes = "已注册机器人列表")
  @PostMapping(value = "/waitbotlist")
  @ResponseBody
  public R botList() {
    List<Map<String, Object>> rtn = new ArrayList<Map<String, Object>>();
    HashSet<BotModel> candidateSet = BotBus.getCandidateSet();
    candidateSet.forEach(
        bot -> {
          Map<String, Object> oneBot = fetchBotInfo(bot);
          rtn.add(oneBot);
        });
    return R.responseBySuccess(rtn);
  }

  @NotNull
  private Map<String, Object> fetchBotInfo(BotModel bot) {
    Map<String, Object> oneBot = new HashMap<String, Object>();
    oneBot.put("id", bot.getId());
    oneBot.put("name", bot.getName());
    oneBot.put("description", bot.getDescription());
    oneBot.put("createdby", bot.getCreatedBy());
    oneBot.put("createdon", DateUtils.toDateString(bot.getCreatedTime()));
    oneBot.put("singleton", bot.isSingleton());

    List<Map<String, Object>> bts = new ArrayList<Map<String, Object>>();
    Arrays.stream(bot.getBotStrategy())
        .forEach(
            btm -> {
              Map<String, Object> bt = new HashMap<String, Object>();
              bt.put("name", btm.getName());
              bt.put("type", btm.getClass().getSimpleName());
              bts.add(bt);
            });
    oneBot.put("botstrategy", bts);

    List<Map<String, Object>> nds = new ArrayList<Map<String, Object>>();
    TreeNode curNode = bot.getJobNode().getRoot();
    if (curNode != null && curNode.getNextNode() != null) {
      do {
        Map<String, Object> nd = new HashMap<String, Object>();
        nd.put("id", curNode.getId());
        nd.put("tag", curNode.getTag());
        nd.put("type", curNode.getClass().getSimpleName());
        nd.put("parentnode", curNode.getParent() == null ? null : curNode.getParent().getId());
        nd.put("showname", curNode.getShowName());
        nds.add(nd);
        curNode = curNode.getNextNode();
      } while (curNode != null);
    }
    oneBot.put("jobnodes", nds);
    return oneBot;
  }

  @ApiOperation(value = "删除已注册机器人", notes = "删除已注册机器人")
  @PostMapping(value = "/removebot")
  @ResponseBody
  public R removeBot(@RequestBody BotInfoDTO dto) {
    HashSet<BotModel> candidateSet = BotBus.getCandidateSet();
    candidateSet.removeIf(bot -> bot.getId().equals(dto.getId()));
    return R.responseBySuccess();
  }

  @ApiOperation(value = "执行中机器人列表", notes = "执行中机器人列表")
  @PostMapping(value = "/runningbotlist")
  @ResponseBody
  public R runningBotList() {
    List<Map<String, Object>> runningSet = new ArrayList<Map<String, Object>>();
    HashSet<BotInstance> runningbots = BotBus.getExecutingBot();
    runningbots.forEach(
        bot -> {
          Map<String, Object> oneBot = fetchBotInfo(bot);
          runningSet.add(oneBot);
        });
    return R.responseBySuccess(runningSet);
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
  public R createUser(@RequestBody UserDTO userObj) {
    try {
      userService.createUser(
          userObj.getUsername(), userObj.getEmail(), userObj.getPassword(), userObj.isAdmin());
      return R.responseBySuccess();
    } catch (Exception e) {
      return R.responseByError(500, e.getMessage());
    }
  }

  @ApiOperation(value = "机器人平台修改用户", notes = "机器人平台修改用户")
  @PostMapping(value = "/modifyuser")
  @ResponseBody
  public R modifyUser(@RequestBody UserDTO userObj) {
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
  public R removeUser(@RequestBody String username) {
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
  public R verifyEmail(@RequestBody UserDTO userObj) {
    try {
      userService.verityEmail(userObj.getUsername(), userObj.getVerifycode());
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
