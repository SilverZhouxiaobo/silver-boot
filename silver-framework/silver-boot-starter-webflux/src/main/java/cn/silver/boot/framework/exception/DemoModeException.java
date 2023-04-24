package cn.silver.boot.framework.exception;

import cn.silver.boot.framework.constant.ResponseEnum;
import lombok.Data;

/**
 * 演示模式异常
 *
 * @author zhouxiaobo
 */
@Data
public class DemoModeException extends GlobalException {
    private static final long serialVersionUID = 1L;

    public DemoModeException() {
        super(ResponseEnum.DEMO_MODE);
    }
}
