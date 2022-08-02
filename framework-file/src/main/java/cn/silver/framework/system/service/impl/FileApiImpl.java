package cn.silver.framework.system.service.impl;

import cn.silver.framework.core.api.IFileApi;
import cn.silver.framework.file.config.FileConfig;
import cn.silver.framework.file.constant.FileUploadStatus;
import cn.silver.framework.file.util.CommonUtils;
import cn.silver.framework.system.domain.SysAccessory;
import cn.silver.framework.system.service.ISysAccessoryService;
import cn.silver.framework.system.vo.SysAccessoryConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileApiImpl implements IFileApi {
    @Autowired
    private ISysAccessoryService accessoryService;

    @Override
    public String saveFile(MultipartFile file, String fileType, String buzPath) {
        String path = CommonUtils.upload(file, buzPath, FileConfig.uploadType);
        SysAccessory accessory = SysAccessoryConvert.getInstance(file, fileType, path);
        accessory.setStatus(FileUploadStatus.COMPLETED.getCode());
        this.accessoryService.insert(accessory);
        return accessory.getId();
    }
}
