package cn.silver.framework.file.constant;

import cn.silver.framework.core.constant.BaseContant;

public enum FileUploadStatus implements BaseContant {
    /**
     * 待上传
     */
    SAVED("01", "待上传"),
    /**
     * 上传中
     */
    UPLOADING("02", "上传中"),
    /**
     * 上传完成
     */
    COMPLETED("03", "上传完成");
    /**
     * 编码
     */
    private final String code;
    /**
     * 名称
     */
    private final String name;

    FileUploadStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }
}
