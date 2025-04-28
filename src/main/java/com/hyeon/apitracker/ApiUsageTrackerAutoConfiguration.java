package com.hyeon.apitracker;

import com.hyeon.apitracker.aspect.ApiUsageAspect;
import com.hyeon.apitracker.config.ApiUsageTrackerProperties;
import com.hyeon.apitracker.controller.ApiUsageMetricsController;
import com.hyeon.apitracker.service.CallCounterService;
import com.hyeon.apitracker.service.MemoryCallCounterService;
import com.hyeon.apitracker.service.RedisCallCounterService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * API Usage Tracker 자동 설정 클래스.
 */
@Configuration
@EnableAspectJAutoProxy
@Import(ApiUsageMetricsController.class)
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

  @Bean
  @ConditionalOnMissingBean
  public ApiUsageAspect apiUsageAspect(CallCounterService callCounterService) {
    return new ApiUsageAspect(callCounterService);
  }
}
