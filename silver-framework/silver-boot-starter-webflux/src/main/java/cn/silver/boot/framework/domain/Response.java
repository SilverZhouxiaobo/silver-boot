package cn.silver.boot.framework.domain;

import cn.silver.boot.framework.constant.ResponseEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 【Response返回实体类】
 *
 * @author JuniorRay
 * @date 2020-11-12
 */
@Data
@Schema(name = "response结果集", description = "http请求返回结果封装对象")
public class Response<T> {

    private static final long serialVersionUID = 1L;

    @Schema(name = "状态码", description = "http请求返回状态码", defaultValue = "200")
    private Integer code;

    @Schema(name = "返回消息说明", description = "http请求返回结果说明", defaultValue = "请求成功")
    private String msg;

    @Schema(name = "数据对象", defaultValue = "http请求返回数据集")
    private T data;

    /**
     * 构造方法私有化，不允许外部new Response
     *
     * @param data
     */
    private Response(T data) {
        this.code = ResponseEnum.SUCCESS.getCode();
        this.msg = ResponseEnum.SUCCESS.getMessage();
        this.data = data;
    }

    private Response(String msg, T data) {
        this.code = ResponseEnum.SUCCESS.getCode();
        this.msg = msg;
        this.data = data;
    }

    private Response(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 构造方法私有化，不允许外部new Response
     *
     * @param responseEnum
     */
    private Response(ResponseEnum responseEnum) {
        if (null == responseEnum) {
            return;
        }
        this.code = responseEnum.getCode();
        this.msg = responseEnum.getMessage();
    }

    private Response(String msg) {
        this.code = 500;
        this.msg = msg;
    }

    public Response(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 成功时调用
     *
     * @param
     * @param <T>
     * @return
     */
    public static <T> Response<T> success() {
        return success(null);
    }

    /**
     * 成功时调用
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Response<T> success(T data) {
        return new Response<>(data);
    }

    public static <T> Response<T> success(String msg, T data) {
        return new Response<>(msg, data);
    }

    /**
     * 失败时调用
     *
     * @param
     * @param <T>
     * @return
     */
    public static <T> Response<T> error() {
        return error(ResponseEnum.ERROR);
    }

    /**
     * 失败时调用
     *
     * @param responseEnum
     * @param <T>
     * @return
     */
    public static <T> Response<T> error(ResponseEnum responseEnum) {
        return new Response<>(responseEnum);
    }

    public static <T> Response<T> error(String msg) {
        return new Response<>(msg);
    }

    public static <T> Response<T> error(int code, String msg) {
        return new Response<>(code, msg);
    }

    public boolean isSuccess() {
        return ResponseEnum.SUCCESS.getCode().equals(this.code);
    }
}
