package jp.co.gutingjun.rpa;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import jp.co.gutingjun.common.util.SnowflakeId;
import jp.co.gutingjun.common.util.SpringBeanUtil;
import jp.co.gutingjun.common.util.WxMappingJackson2HttpMessageConverter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.client.RestTemplate;
import org.xnio.OptionMap;
import org.xnio.Xnio;
import org.xnio.XnioWorker;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableDiscoveryClient
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ServletComponentScan
@EnableFeignClients
@EnableHystrix
@Slf4j
@EnableAsync
public class GtjRpaApplication {
  public static void main(String[] args) {
    SpringApplication.run(GtjRpaApplication.class, args);
  }

  @Bean
  public SnowflakeId init() {
    return new SnowflakeId(1);
  }

  @Bean
  public RestTemplate restTemplate(@Qualifier("simpleClientHttpRequestFactory") ClientHttpRequestFactory factory) {
    RestTemplate restTemplate = new RestTemplate(factory);
    restTemplate.getMessageConverters().add(new WxMappingJackson2HttpMessageConverter());
    return restTemplate;
  }

  @Bean
  public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    // 单位为ms
    factory.setReadTimeout(5000);
    // 单位为ms
    factory.setConnectTimeout(5000);
    return factory;
  }

  @Bean
  public SpringBeanUtil getSpringBeanUtil(ApplicationContext applicationContext) {
    SpringBeanUtil springBeanUtil = new SpringBeanUtil();
    springBeanUtil.setApplicationContext(applicationContext);
    return springBeanUtil;
  }

  @Bean
  public OkHttpClient okHttpClient() {
    return new OkHttpClient.Builder()
        // 设置连接超时
        .connectTimeout(10, TimeUnit.SECONDS)
        // 设置读超时
        .readTimeout(10, TimeUnit.SECONDS)
        // 设置写超时
        .writeTimeout(10, TimeUnit.SECONDS)
        // 是否自动重连
        .retryOnConnectionFailure(true)
        .connectionPool(new ConnectionPool(10, 5L, TimeUnit.MINUTES))
        .build();
  }

  @Bean
  public EmbeddedServletContainerCustomizer containerCustomizer() {
    return container -> {
      UndertowEmbeddedServletContainerFactory factory =
          (UndertowEmbeddedServletContainerFactory) container;
      factory.addDeploymentInfoCustomizers(
          deploymentInfo -> {
            WebSocketDeploymentInfo webSocketDeploymentInfo = new WebSocketDeploymentInfo();
            webSocketDeploymentInfo.setBuffers(new DefaultByteBufferPool(false, 1024));
            final Xnio xnio = Xnio.getInstance("nio", Undertow.class.getClassLoader());
            try {
              final XnioWorker xnioWorker = xnio.createWorker(OptionMap.builder().getMap());
              webSocketDeploymentInfo.setWorker(xnioWorker);
            } catch (Exception e) {
              log.error("containerCustomizer", e);
            }
            deploymentInfo.addServletContextAttribute(
                "io.undertow.websockets.jsr.WebSocketDeploymentInfo", webSocketDeploymentInfo);
          });
      factory.addBuilderCustomizers(
          builder -> builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true));
    };
  }
}