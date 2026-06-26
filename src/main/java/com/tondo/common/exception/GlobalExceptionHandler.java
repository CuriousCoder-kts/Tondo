package com.tondo.common.exception;

import com.tondo.common.response.Result;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusiness(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return Result.error(400, message.isEmpty() ? "参数校验失败" : message);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<?> handleAccessDenied(AccessDeniedException e) {
        return Result.error(403, "权限不足");
    }

    @ExceptionHandler(AuthenticationException.class)
    public Result<?> handleAuthentication(AuthenticationException e) {
        return Result.error(401, "未登录或登录已失效");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result<?> handleDuplicate(DuplicateKeyException e) {
        return Result.error(400, "操作重复，请勿重复提交");
    }

    @ExceptionHandler({MultipartException.class, MaxUploadSizeExceededException.class})
    public Result<?> handleMultipart(Exception e) {
        return Result.error(400, "文件上传格式错误或文件过大");
    }

    @ExceptionHandler(DataAccessException.class)
    public Result<?> handleDataAccess(DataAccessException e) {
        return Result.error(500, "数据库操作失败，请确认表结构已迁移");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleUnreadable(HttpMessageNotReadableException e) {
        return Result.error(400, "请求体格式错误");
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleGeneral(Exception e) {
        return Result.error(500, "服务器繁忙，请稍后重试");
    }
}
