package com.hyeon.apitracker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * application.yml에서 설정을 읽어오는 프로퍼티 클래스.
 */
@ConfigurationProperties(prefix = "api-tracker")
public class ApiUsageTrackerProperties {

  /**
   * 저장 방식 (redis, memory)
   */
  private String storage = "redis";

  /**
   * TTL (Time to Live) 분 단위 (default: 1440분 = 1일)
   */
  private long ttlMinutes = 1440;

  public String getStorage() {
    return storage;
  }

  public void setStorage(String storage) {
    this.storage = storage;
  }

  public long getTtlMinutes() {
    return ttlMinutes;
  }

  public void setTtlMinutes(long ttlMinutes) {
    this.ttlMinutes = ttlMinutes;
  }
}
