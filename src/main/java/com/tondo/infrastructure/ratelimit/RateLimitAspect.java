package com.tondo.infrastructure.ratelimit;

import com.tondo.common.annotation.RateLimit;
import com.tondo.common.enums.ErrorCode;
import com.tondo.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RateLimitService rateLimitService;

    @Around("@annotation(limit)")
    public Object around(ProceedingJoinPoint pjp, RateLimit limit) throws Throwable {
        String clientKey = resolveClientIp();
        String redisKey = "rate:" + limit.key() + ":" + clientKey;
        if (!rateLimitService.tryAcquire(redisKey, limit.limit(), limit.seconds())) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS.getCode(), ErrorCode.TOO_MANY_REQUESTS.getDefaultMessage());
        }
        return pjp.proceed();
    }

    private String resolveClientIp() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return "unknown";
        }
        HttpServletRequest request = attrs.getRequest();
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
