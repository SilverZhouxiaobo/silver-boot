package cn.silver.framework.core.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 【Response返回实体类】
 *
 * @author JuniorRay
 * @date 2020-11-12
 */
@Data
@ApiModel(description = "结果返回类")
public class Response<T> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "状态码")
    private Integer code;

    @ApiModelProperty(value = "返回内容")
    private String msg;

    @ApiModelProperty(value = "数据对象")
    private T data;

    @ApiModelProperty(value = "请求接口名称")
    private String title;

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

    public Response(String msg) {
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
