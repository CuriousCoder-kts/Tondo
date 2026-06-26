package com.tondo.infrastructure.audit;

import com.tondo.common.annotation.OperationLog;
import com.tondo.module.governance.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;
    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(opLog)")
    public Object around(ProceedingJoinPoint pjp, OperationLog opLog) throws Throwable {
        Object result = pjp.proceed();

        try {
            Long operatorId = resolveOperatorId(pjp);
            Long targetId = resolveTargetId(pjp, opLog.targetIdSpel());
            String ip = resolveClientIp();
            String detail = opLog.action() + " result=success";
            operationLogService.record(
                    operatorId,
                    opLog.action(),
                    opLog.targetType(),
                    targetId,
                    detail,
                    ip);
        } catch (Exception ignored) {
            // 审计失败不影响主业务
        }

        return result;
    }

    private Long resolveOperatorId(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String[] names = signature.getParameterNames();
        Object[] args = pjp.getArgs();
        for (int i = 0; i < names.length; i++) {
            if (("handlerId".equals(names[i]) || "userId".equals(names[i])) && args[i] instanceof Long) {
                return (Long) args[i];
            }
        }
        for (Object arg : args) {
            if (arg instanceof Long) {
                return (Long) arg;
            }
        }
        return null;
    }

    private Long resolveTargetId(ProceedingJoinPoint pjp, String spel) {
        if (spel == null || spel.isBlank()) {
            return null;
        }
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        String[] names = signature.getParameterNames();
        Object[] args = pjp.getArgs();
        for (int i = 0; i < names.length; i++) {
            ctx.setVariable(names[i], args[i]);
        }
        Object value = parser.parseExpression(spel).getValue(ctx);
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }

    private String resolveClientIp() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }
        HttpServletRequest request = attrs.getRequest();
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
