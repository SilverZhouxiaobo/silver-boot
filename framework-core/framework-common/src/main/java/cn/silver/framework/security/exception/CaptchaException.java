package cn.silver.framework.security.exception;

import cn.silver.framework.common.exception.user.UserException;

/**
 * 验证码错误异常类
 *
 * @author hb
 */
public class CaptchaException extends UserException {
    private static final long serialVersionUID = 1L;

    public CaptchaException() {
        super("user.jcaptcha.error", null);
    }
}
