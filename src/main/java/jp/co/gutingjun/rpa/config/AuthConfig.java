package jp.co.gutingjun.rpa.config;

import jp.co.gutingjun.rpa.impl.AuthInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 登录验证配置适配器
 *
 * @author sunsx
 */
@Configuration
public class AuthConfig extends WebMvcConfigurerAdapter {
  @Bean
  public AuthInterceptor initAuthInterceptor() {
    return new AuthInterceptor();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(initAuthInterceptor())
        .addPathPatterns("/rpa/**")
        .excludePathPatterns("/rpa/login");
  }
}
