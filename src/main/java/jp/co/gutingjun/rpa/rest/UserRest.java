package jp.co.gutingjun.rpa.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jp.co.gutingjun.common.util.R;
import jp.co.gutingjun.rpa.inf.IUserService;
import jp.co.gutingjun.rpa.rest.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

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
public class UserRest {
  @Autowired IUserService userService;

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
