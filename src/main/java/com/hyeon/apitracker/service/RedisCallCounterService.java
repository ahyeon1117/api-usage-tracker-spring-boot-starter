package com.hyeon.apitracker.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * Redis를 사용하여 API 호출 수를 기록하고 조회하는 서비스 구현체.
 */
public class RedisCallCounterService implements CallCounterService {

  private static final String PREFIX = "usage";
  private final StringRedisTemplate redisTemplate;
  private final long ttlMinutes;

  public RedisCallCounterService(
    StringRedisTemplate redisTemplate,
    long ttlMinutes
  ) {
    this.redisTemplate = redisTemplate;
    this.ttlMinutes = ttlMinutes;
  }

  @Override
  public void increment(String apiName) {
    String key = buildKey(apiName, LocalDateTime.now(), "hourly");
    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    ops.increment(key);
    redisTemplate.expire(key, ttlMinutes, TimeUnit.MINUTES);
  }

  @Override
  public Map<String, Integer> getUsage(String range) {
    Set<String> keys = redisTemplate.keys(PREFIX + ":*");
    Map<String, Integer> result = new HashMap<>();

    if (keys != null) {
      ValueOperations<String, String> ops = redisTemplate.opsForValue();
      for (String key : keys) {
        if (isKeyInRange(key, range)) {
          String value = ops.get(key);
          if (value != null) {
            String apiName = extractApiName(key);
            result.put(
              apiName,
              result.getOrDefault(apiName, 0) + Integer.parseInt(value)
            );
          }
        }
      }
    }

    return result;
  }

  @Override
  public String buildKey(
    String apiName,
    LocalDateTime timestamp,
    String range
  ) {
    String timePart;
    if ("daily".equalsIgnoreCase(range)) {
      timePart = timestamp.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    } else if ("hourly".equalsIgnoreCase(range)) {
      timePart = timestamp.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
    } else if ("5min".equalsIgnoreCase(range)) {
      int minute = (timestamp.getMinute() / 5) * 5;
      timePart =
        timestamp.format(DateTimeFormatter.ofPattern("yyyyMMddHH")) +
        String.format("%02d", minute);
    } else {
      // 기본은 hourly
      timePart = timestamp.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
    }
    return String.format("%s:%s:%s", PREFIX, apiName, timePart);
  }

  private boolean isKeyInRange(String key, String range) {
    if (range.equalsIgnoreCase("daily")) {
      return key.matches("usage:.+:\\d{8}$");
    } else if (range.equalsIgnoreCase("hourly")) {
      return key.matches("usage:.+:\\d{10}$");
    } else if (range.equalsIgnoreCase("5min")) {
      return key.matches("usage:.+:\\d{12}$");
    }
    return false;
  }

  private String extractApiName(String key) {
    String[] parts = key.split(":");
    if (parts.length >= 3) {
      return parts[1];
    }
    return "unknown";
  }
}
