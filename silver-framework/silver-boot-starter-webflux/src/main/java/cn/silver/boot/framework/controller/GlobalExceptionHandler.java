package cn.silver.boot.framework.controller;


import cn.silver.boot.framework.constant.ResponseEnum;
import cn.silver.boot.framework.domain.Response;
import cn.silver.boot.framework.exception.CustomValidateException;
import cn.silver.boot.framework.exception.DemoModeException;
import cn.silver.boot.framework.exception.GlobalException;
import cn.silver.boot.framework.exception.UtilException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpRequest;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.nio.file.AccessDeniedException;


/**
 * 全局异常处理器
 *
 * @author hb
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class)
    public Response<Void> handleDemoModeException(DemoModeException e) {
        return Response.error("演示模式，不允许操作");
    }

    /**
     * 权限校验异常
     *
     * @return
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Response<Void> handleAccessDeniedException(AccessDeniedException e, ServerRequest request) {
        String requestURI = request.uri().getPath();
        log.error("请求地址{},权限校验失败{}", requestURI, e.getMessage());
        return Response.error(ResponseEnum.FORBIDDEN.getCode(), "没有权限，请联系管理员授权");
    }

    /**
     * 请求方式不支持
     */
//    @ExceptionHandler(ServerR.class)
//    public Response<Void> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpRequest request) {
//        String requestURI = request.getURI().getPath();
//        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod(), e);
//        return Response.error(String.format("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod()));
//    }

    /**
     * 工具类异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(UtilException.class)
    public Response<Void> handleUserException(UtilException e) {
        log.error(e.getMessage(), e);
        return Response.error(e.getMessage());
    }

    /**
     * 数据校验异常
     */
    @ExceptionHandler(CustomValidateException.class)
    public Response<Void> handleServiceException(CustomValidateException e) {
        Integer code = e.getCode();
        return ObjectUtils.isNotEmpty(code) ? Response.error(code, e.getMessage()) : Response.error(e.getMessage());
    }

    /**
     * 业务服务异常
     */
    @ExceptionHandler(GlobalException.class)
    public Response<Void> handleCustomException(GlobalException e) {
        Integer code = e.getCode();
        return ObjectUtils.isNotEmpty(code) ? Response.error(code, e.getMessage()) : Response.error(e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public Response<Void> handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return Response.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return Response.error(message);
    }


    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Response<Void> handleRuntimeException(RuntimeException e, HttpRequest request) {
        String requestURI = request.getURI().getPath();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        return Response.error(String.format("请求地址'{}',发生未知异常.", requestURI));
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public Response<Void> handleException(Exception e, HttpRequest request) {
        String requestURI = request.getURI().getPath();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return Response.error(String.format("请求地址'{}',发生系统异常.", requestURI));
    }
}
