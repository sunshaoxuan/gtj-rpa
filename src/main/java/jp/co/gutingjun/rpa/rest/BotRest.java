package jp.co.gutingjun.rpa.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.undertow.util.DateUtils;
import jp.co.gutingjun.common.pms.TreeNode;
import jp.co.gutingjun.common.util.R;
import jp.co.gutingjun.rpa.application.BotBus;
import jp.co.gutingjun.rpa.model.bot.BotInstance;
import jp.co.gutingjun.rpa.model.bot.BotModel;
import jp.co.gutingjun.rpa.model.bot.IBot;
import jp.co.gutingjun.rpa.model.jobflow.condition.LogicalConditionModel;
import jp.co.gutingjun.rpa.model.jobflow.node.BaseLinkNode;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 机器人服务
 *
 * @author sunsx
 */
@RestController
@Slf4j
@Api(
    value = "rpa rest",
    tags = {"rpa"})
@RequestMapping("rpa")
public class BotRest {
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
      IBot bot = BotBus.getInstance().botRegister(botContext);
      return R.responseBySuccess("RPA bot [BotID:" + bot.getId() + "] has been registered.");
    } catch (Exception e) {
      return R.responseByError(500, e.getMessage());
    }
  }

  @ApiOperation(value = "已注册机器人列表", notes = "已注册机器人列表")
  @PostMapping(value = "/waitbotlist")
  @ResponseBody
  public R botList() {
    List<Map<String, Object>> rtn = new ArrayList<Map<String, Object>>();
    HashSet<BotModel> candidateSet = BotBus.getInstance().getCandidateSet();
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
        if (curNode instanceof BaseLinkNode) {
          LogicalConditionModel con =
              (LogicalConditionModel) ((BaseLinkNode) curNode).getRuleCondition();
          if (con != null) {
            HashMap<Object, Object> condMap = new HashMap<>();
            condMap.put("left", con.getLeft());
            condMap.put("right", con.getRight());
            condMap.put("operator", con.getOperator());
            nd.put("condition", condMap);
          }
        }
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
    HashSet<BotModel> candidateSet = BotBus.getInstance().getCandidateSet();
    candidateSet.removeIf(bot -> bot.getId().equals(dto.getId()));
    return R.responseBySuccess();
  }

  @ApiOperation(value = "执行中机器人列表", notes = "执行中机器人列表")
  @PostMapping(value = "/runningbotlist")
  @ResponseBody
  public R runningBotList() {
    List<Map<String, Object>> runningSet = new ArrayList<Map<String, Object>>();
    HashSet<BotInstance> runningbots = BotBus.getInstance().getExecutingBot();
    runningbots.forEach(
        bot -> {
          Map<String, Object> oneBot = fetchBotInfo(bot);
          runningSet.add(oneBot);
        });
    return R.responseBySuccess(runningSet);
  }

  @ApiOperation(value = "注册并运行机器人", notes = "注册并运行机器人")
  @PostMapping(value = "/botregandstart")
  @ResponseBody
  public R regAndRunBot(@RequestBody String botContext) {
    try {
      BotModel bot = BotBus.getInstance().botRegister(botContext);
      BotBus.getInstance().manualExecuteBot(bot.getId());
    } catch (Exception e) {
      return R.responseByError(500, e.getMessage());
    }

    return R.responseBySuccess();
  }

  @ApiOperation(value = "机器人手工启动", notes = "机器人手工启动")
  @PostMapping(value = "/botmanualstart")
  @ResponseBody
  public R botStart(@RequestBody BotInfoDTO dto) {
    BotBus.getInstance().manualExecuteBot(dto.getId());
    return R.responseBySuccess();
  }
}
