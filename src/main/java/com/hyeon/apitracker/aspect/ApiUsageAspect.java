package com.hyeon.apitracker.aspect;

import com.hyeon.apitracker.service.CallCounterService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Controller 레이어의 API 호출을 감지해서 사용량을 기록하는 AOP Aspect.
 */
@Aspect
@Component
public class ApiUsageAspect {

  private final CallCounterService callCounterService;

  public ApiUsageAspect(CallCounterService callCounterService) {
    this.callCounterService = callCounterService;
  }

  @AfterReturning(
    "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
    "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
    "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
    "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
    "@annotation(org.springframework.web.bind.annotation.RequestMapping)"
  )
  public void afterApiCall(JoinPoint joinPoint) {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      HttpServletRequest request = attributes.getRequest();
      String path = request.getRequestURI();

      System.out.println("🔥 AOP 트리거! 요청 경로: " + path);

      callCounterService.increment(path);
    } else {
      System.out.println("⚠️ AOP 트리거! but RequestAttributes가 null입니다.");
    }
  }
}
