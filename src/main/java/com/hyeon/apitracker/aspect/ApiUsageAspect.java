package com.hyeon.apitracker.aspect;

import com.hyeon.apitracker.service.CallCounterService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Controller 레이어의 API 호출을 감지해서 사용량을 기록하는 AOP Aspect.
 */
@Aspect
public class ApiUsageAspect {

  private final CallCounterService callCounterService;

  public ApiUsageAspect(CallCounterService callCounterService) {
    this.callCounterService = callCounterService;
  }

  @AfterReturning("@within(requestMapping) || @annotation(requestMapping)")
  public void afterApiCall(JoinPoint joinPoint, RequestMapping requestMapping) {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      HttpServletRequest request = attributes.getRequest();
      String path = request.getRequestURI();
      callCounterService.increment(path);
    }
  }
}
