package cn.silver.framework.security.exception;

import cn.silver.framework.common.exception.user.UserException;

/**
 * 验证码失效异常类
 *
 * @author hb
 */
public class CaptchaExpireException extends UserException {
    private static final long serialVersionUID = 1L;

    public CaptchaExpireException() {
        super("user.jcaptcha.expire", null);
    }
}
