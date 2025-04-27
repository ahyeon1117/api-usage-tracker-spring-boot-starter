package com.hyeon.apitracker;

import com.hyeon.apitracker.config.ApiUsageTrackerProperties;
import com.hyeon.apitracker.service.CallCounterService;
import com.hyeon.apitracker.service.MemoryCallCounterService;
import com.hyeon.apitracker.service.RedisCallCounterService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * API Usage Tracker 자동 설정 클래스.
 */
@Configuration
@EnableConfigurationProperties(ApiUsageTrackerProperties.class)
public class ApiUsageTrackerAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public CallCounterService callCounterService(
    ApiUsageTrackerProperties properties,
    StringRedisTemplate redisTemplate
  ) {
    if ("redis".equalsIgnoreCase(properties.getStorage())) {
      return new RedisCallCounterService(
        redisTemplate,
        properties.getTtlMinutes()
      );
    } else {
      return new MemoryCallCounterService();
    }
  }
}
