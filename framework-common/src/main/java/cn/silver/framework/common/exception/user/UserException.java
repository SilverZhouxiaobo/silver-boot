package cn.silver.framework.common.exception.user;

import cn.silver.framework.common.exception.base.BaseException;

/**
 * 用户信息异常类
 *
 * @author hb
 */
public class UserException extends BaseException {
    private static final long serialVersionUID = 1L;

    public UserException(String code, Object[] args) {
        super("user", code, args, null);
    }
}
