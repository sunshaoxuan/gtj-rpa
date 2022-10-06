package jp.co.gutingjun.rpa.impl;

import jp.co.gutingjun.rpa.config.BotConfig;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
  @Resource private RedisTemplate<String, Object> redisTemplate;

  public void set(String key, Object value) {
    // 更改在redis里面查看key编码问题
    RedisSerializer redisSerializer = new StringRedisSerializer();
    redisTemplate.setKeySerializer(redisSerializer);
    ValueOperations<String, Object> vo = redisTemplate.opsForValue();
    vo.set(key, value, BotConfig.TK_EXPIRE_HOURS, TimeUnit.HOURS);
  }

  public Object get(String key) {
    ValueOperations<String, Object> vo = redisTemplate.opsForValue();
    return vo.get(key);
  }

  public void delete(String key) {
    redisTemplate.delete(key);
  }
}