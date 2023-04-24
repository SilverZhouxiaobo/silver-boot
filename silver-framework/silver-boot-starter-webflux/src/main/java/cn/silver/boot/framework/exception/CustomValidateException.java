package cn.silver.boot.framework.exception;


import cn.silver.boot.framework.constant.ResponseEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据校验异常
 *
 * @author zhouxiaobo
 */
@Data
@NoArgsConstructor
public class CustomValidateException extends GlobalException {
    public CustomValidateException(String message) {
        super(ResponseEnum.DATA_VALIDATED_FAILED, message);
    }
}
