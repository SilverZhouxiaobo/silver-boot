package cn.silver.framework.system.vo;


import cn.silver.framework.file.config.FileConfig;
import cn.silver.framework.file.util.FileUtils;
import cn.silver.framework.system.domain.SysAccessory;
import cn.silver.framework.system.domain.SysAccessorySlice;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

public class SysAccessoryConvert {
    public static SysAccessory getInstance(String sign) {
        SysAccessory accessory = new SysAccessory();
        accessory.setMd5(sign);
        return accessory;
    }

    @SneakyThrows
    public static SysAccessory getInstance(MultipartFile file, String fileType, String path) {
        SysAccessory accessory = new SysAccessory();
        accessory.setType(fileType);
        accessory.setLocation(path);
        accessory.setSign(FileUtils.getMd5(file.getInputStream()));
        accessory.setBaseName(file.getName());
        accessory.setName(file.getName());
        accessory.setSaveMode(FileConfig.uploadType);
        accessory.setFormat(file.getContentType());
        return accessory;
    }

    public static SysAccessorySlice getSlice(SysAccessory accessory, Integer chunk, String location) {
        SysAccessorySlice slice = new SysAccessorySlice();
        slice.setAccessory(accessory.getId());
        slice.setChunkNo(chunk);
        slice.setLocation(location);
        return slice;
    }
}
