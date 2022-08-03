package cn.silver.framework.common.exception;

import cn.silver.framework.core.bean.ResponseEnum;

/**
 * 自定义异常
 *
 * @author hb
 */
public class CustomException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Integer code;

    private final String message;

    public CustomException(ResponseEnum response) {
        this.message = response.getMessage();
        this.code = response.getCode();
    }

    public CustomException(String message) {
        this.message = message;
        this.code = ResponseEnum.DATA_VALIDATED_FAILED.getCode();
    }

    public CustomException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public CustomException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
