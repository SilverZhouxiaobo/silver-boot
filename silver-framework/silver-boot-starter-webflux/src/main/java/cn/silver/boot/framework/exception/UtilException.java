package cn.silver.boot.framework.exception;

/**
 * 工具类异常
 *
 * @author zhouxiaobo
 */
public class UtilException extends GlobalException {
    private static final long serialVersionUID = 1L;
    public UtilException(String message) {
        super(message);
    }
    public UtilException(Throwable e) {
        super(e.getMessage());
    }

}
