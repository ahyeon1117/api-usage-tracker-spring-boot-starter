package com.hyeon.apitracker.service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * API 호출 수를 기록하고 조회하는 서비스 인터페이스.
 */
public interface CallCounterService {
  /**
   * 주어진 API 이름의 호출 수를 현재 시간대 기준으로 1 증가시킨다.
   * @param apiName 호출된 API 이름
   */
  void increment(String apiName);

  /**
   * 주어진 시간 범위에서 호출 수를 조회한다.
   * @param range 조회 범위 (daily, hourly, 5min 등)
   * @return API별 호출 수 맵
   */
  Map<String, Integer> getUsage(String range);

  /**
   * 내부적으로 사용하는 키를 생성한다.
   * @param apiName API 이름
   * @param timestamp 현재 시간
   * @param range 시간 범위 (daily, hourly, 5min 등)
   * @return 저장/조회용 키
   */
  String buildKey(String apiName, LocalDateTime timestamp, String range);
}
