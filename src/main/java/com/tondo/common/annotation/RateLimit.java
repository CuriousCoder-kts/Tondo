package com.tondo.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Redis 滑动窗口限流（按 IP + 业务 key）。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /** 限流维度，如 login、register */
    String key();

    /** 时间窗口内最大请求数 */
    int limit() default 20;

    /** 时间窗口秒数 */
    int seconds() default 60;
}
