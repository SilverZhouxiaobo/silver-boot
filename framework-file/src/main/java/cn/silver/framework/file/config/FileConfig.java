package cn.silver.framework.file.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件处理相关配置
 */
@Component
@ConfigurationProperties(prefix = "file")
public class FileConfig {
    public static String uploadType;
    public static String uploadPath;
    public static String webapp;
    public static String packagePath;
    public static String template;

    @Value(value = "${file.uploadType}")
    public void setUploadType(String uploadType) {
        FileConfig.uploadType = uploadType;
    }

    @Value(value = "${file.path.upload}")
    public void setUploadPath(String uploadPath) {
        FileConfig.uploadPath = uploadPath;
    }

    @Value(value = "${file.path.webapp}")
    public void setWebapp(String webapp) {
        FileConfig.webapp = webapp;
    }

    @Value(value = "${file.path.package}")
    public void setPackagePath(String packagePath) {
        FileConfig.packagePath = packagePath;
    }

    @Value(value = "${file.path.template}")
    public void setTemplate(String template) {
        FileConfig.template = template;
    }
}
