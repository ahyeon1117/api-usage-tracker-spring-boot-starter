package com.hyeon.apitracker.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 메모리를 사용하여 API 호출 수를 기록하고 조회하는 서비스 구현체.
 */
public class MemoryCallCounterService implements CallCounterService {

  private static final String PREFIX = "usage";
  private final Map<String, Integer> counter = new ConcurrentHashMap<>();

  @Override
  public void increment(String apiName) {
    String key = buildKey(apiName, LocalDateTime.now(), "hourly");
    counter.merge(key, 1, Integer::sum);
  }

  @Override
  public Map<String, Integer> getUsage(String range) {
    Map<String, Integer> result = new ConcurrentHashMap<>();
    for (Map.Entry<String, Integer> entry : counter.entrySet()) {
      String key = entry.getKey();
      if (isKeyInRange(key, range)) {
        String apiName = extractApiName(key);
        result.put(apiName, result.getOrDefault(apiName, 0) + entry.getValue());
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
