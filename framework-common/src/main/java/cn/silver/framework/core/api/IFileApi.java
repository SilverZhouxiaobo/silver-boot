package cn.silver.framework.core.api;

import org.springframework.web.multipart.MultipartFile;

public interface IFileApi {

    String saveFile(MultipartFile file, String fileType, String buzPath);
}
