package cn.silver.framework.file.constant;

import cn.silver.framework.core.constant.BaseContant;

public enum FileUploadType implements BaseContant {
    /**
     * 本地文件系统
     */
    LOCAL("local", "本地文件系统"),
    /**
     * Minio分布式存储
     */
    MINIO("minio", "Minio分布式存储"),
    /**
     * 阿里云分布式存储
     */
    OSS("oss", "阿里云分布式存储");

    private final String code;
    private final String name;

    FileUploadType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
