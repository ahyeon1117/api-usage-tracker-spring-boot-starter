package com.hyeon.apitracker.controller;

import com.hyeon.apitracker.service.CallCounterService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * API 호출 수를 조회하는 Metrics 컨트롤러.
 */
@RestController
public class ApiUsageMetricsController {

  private final CallCounterService callCounterService;

  public ApiUsageMetricsController(CallCounterService callCounterService) {
    this.callCounterService = callCounterService;
  }

  /**
   * API 사용량 조회
   * @param range 조회 범위 (daily, hourly, 5min)
   * @return API별 호출 수
   */
  @GetMapping("/metrics/usage")
  public Map<String, Integer> getUsage(
    @RequestParam(defaultValue = "hourly") String range
  ) {
    return callCounterService.getUsage(range);
  }
}
