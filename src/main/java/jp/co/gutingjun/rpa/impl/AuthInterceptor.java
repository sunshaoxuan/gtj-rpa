package jp.co.gutingjun.rpa.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 用户验证拦截器
 *
 * @author sunsx
 * */
public class AuthInterceptor implements HandlerInterceptor {
  @Autowired private RedisService redisService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html;charset=utf-8");
    String token = request.getHeader("token");
    if (StringUtils.isEmpty(token)) {
      response.getWriter().print("User do not login.");
      return false;
    }
    Object loginStatus = redisService.get(token);
    if (Objects.isNull(loginStatus)) {
      response.getWriter().print("token error.");
      return false;
    }
    return true;
  }

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      ModelAndView modelAndView)
      throws Exception {}

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {}
}
