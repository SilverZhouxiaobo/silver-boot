package cn.silver.framework.file.exception;

import cn.silver.framework.common.exception.base.BaseException;

/**
 * 文件信息异常类
 *
 * @author hb
 */
public class FileException extends BaseException {
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args) {
        super("file", code, args, null);
    }

}
