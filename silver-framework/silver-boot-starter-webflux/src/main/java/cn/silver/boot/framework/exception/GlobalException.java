package cn.silver.boot.framework.exception;

import cn.silver.boot.framework.constant.ResponseEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * 全局异常
 *
 * @author zhouxiaobo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    private int code;
    /**
     * 错误提示
     */
    private String message;

    /**
     * 错误明细，内部调试错误
     * <p>
     */
    private String detailMessage;

    public GlobalException(String message) {
        this.code = 500;
        this.message = message;
    }

    public GlobalException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public GlobalException(ResponseEnum responseEnum) {
        this.message = responseEnum.getMessage();
        this.code = responseEnum.getCode();
    }

    public GlobalException(ResponseEnum responseEnum, String message) {
        this.message = message;
        this.code = responseEnum.getCode();
    }
}