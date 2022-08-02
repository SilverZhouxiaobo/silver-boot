package cn.silver.framework.file.config;

import cn.silver.framework.file.minio.MinioUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Minio文件上传配置文件
 */
@Component
@ConfigurationProperties(prefix = "file.minio")
public class MinioConfig {
    @Value(value = "${file.minio.minio_url}")
    private String minioUrl;
    @Value(value = "${file.minio.minio_name}")
    private String minioName;
    @Value(value = "${file.minio.minio_pass}")
    private String minioPass;
    @Value(value = "${file.minio.bucketName}")
    private String bucketName;

    @Bean
    public void initMinio() {
        if (!minioUrl.startsWith("http")) {
            minioUrl = "http://" + minioUrl;
        }
        if (!minioUrl.endsWith("/")) {
            minioUrl = minioUrl.concat("/");
        }
        MinioUtil.setMinioUrl(minioUrl);
        MinioUtil.setMinioName(minioName);
        MinioUtil.setMinioPass(minioPass);
        MinioUtil.setBucketName(bucketName);
    }

}
