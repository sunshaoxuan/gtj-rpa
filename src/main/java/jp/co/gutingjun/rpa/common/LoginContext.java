package jp.co.gutingjun.rpa.common;

import jp.co.gutingjun.rpa.impl.RedisService;
import jp.co.gutingjun.rpa.inf.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class LoginContext {
  private static RedisService rService;
  private static IUserService uService;
  @Autowired RedisService redisService;
  @Autowired IUserService userService;

  /**
   * 获取当前用户名
   *
   * @return
   */
  public static String getUsername() {
    return (String) rService.get(getToken());
  }

  /**
   * 获取当前用户ID
   *
   * @return
   */
  public static Long getUserid() {
    return uService.getUserId(getUsername());
  }

  /**
   * 获取用户请求IP
   *
   * @return
   */
  public static String getIp() {
    return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
        .getRequest()
        .getRemoteAddr();
  }

  /**
   * 获取当前用户Token
   *
   * @return
   */
  public static String getToken() {
    return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
        .getRequest()
        .getHeader("token");
  }

  @PostConstruct
  public void init() {
    rService = redisService;
    uService = userService;
  }
}
