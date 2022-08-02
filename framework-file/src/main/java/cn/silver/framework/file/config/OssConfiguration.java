package cn.silver.framework.file.config;

import cn.silver.framework.file.oss.OssBootUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 云存储 配置
 */
@Component
@ConfigurationProperties(prefix = "file.oss")
public class OssConfiguration {

    @Value("${file.oss.endpoint}")
    private String endpoint;
    @Value("${file.oss.accessKey}")
    private String accessKeyId;
    @Value("${file.oss.secretKey}")
    private String accessKeySecret;
    @Value("${file.oss.bucketName}")
    private String bucketName;
    @Value("${file.oss.staticDomain}")
    private String staticDomain;


    @Bean
    public void initOssBootConfiguration() {
        OssBootUtil.setEndPoint(endpoint);
        OssBootUtil.setAccessKeyId(accessKeyId);
        OssBootUtil.setAccessKeySecret(accessKeySecret);
        OssBootUtil.setBucketName(bucketName);
        OssBootUtil.setStaticDomain(staticDomain);
    }
}