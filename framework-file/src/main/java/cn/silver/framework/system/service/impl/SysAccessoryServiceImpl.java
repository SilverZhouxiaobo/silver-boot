package cn.silver.framework.system.service.impl;

import cn.silver.framework.common.exception.CustomException;
import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.file.config.FileConfig;
import cn.silver.framework.file.constant.FileUploadStatus;
import cn.silver.framework.file.constant.FileUploadType;
import cn.silver.framework.file.util.CommonUtils;
import cn.silver.framework.system.domain.SysAccessory;
import cn.silver.framework.system.domain.SysAccessorySlice;
import cn.silver.framework.system.mapper.SysAccessoryMapper;
import cn.silver.framework.system.service.ISysAccessoryService;
import cn.silver.framework.system.service.ISysAccessorySliceService;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * {@code @Description:}:  附件管理
 * {@code @Author:} 周晓菠
 * {@code @Date:} 2022-01-04
 * {@code @Version:} V1.0
 */
@Service
public class SysAccessoryServiceImpl extends BaseServiceImpl<SysAccessoryMapper, SysAccessory> implements ISysAccessoryService {

    @Autowired
    private ISysAccessorySliceService sliceService;

    @Override
    public int insertOrUpdate(SysAccessory entity) {
        List<SysAccessory> exist = this.selectExists(entity);
        if (CollectionUtils.isEmpty(exist)) {
            entity.setStatus(FileUploadStatus.SAVED.getCode());
            entity.setSliceFlag(entity.getSliceNum() > 1);
            entity.setSaveMode(FileConfig.uploadType);
            String name = entity.getName();
            entity.setBaseName(FilenameUtils.getBaseName(name));
            name = name.replace(entity.getBaseName(), entity.getBaseName() + "_" + System.currentTimeMillis());
            entity.setLocation(entity.getBizPath() + "/" + name);
            if (entity.isSliceFlag() && !FileUploadType.LOCAL.getCode().equals(FileConfig.uploadType)) {
                String uploadId = CommonUtils.getUploadId(FileConfig.uploadType, entity.getLocation(), entity.getFormat());
                entity.setUploadId(uploadId);
            }
            entity.setSuccessChunk(0);
            return this.insert(entity);
        } else if (exist.size() == 1) {
            entity = exist.get(0);
            List<SysAccessorySlice> slices = this.sliceService.selectByMainId(entity.getId());
            entity.setSliceFlag(entity.getSliceNum() > 1);
            entity.setSuccessChunk(slices.size());
            entity.setStatus(FileUploadStatus.UPLOADING.getCode());
            return 0;
        } else {
            throw new CustomException("存在多条数据无法更新");
        }
    }

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int delete(String id) {
        SysAccessory accessory = this.selectById(id);
        if (!FileUploadStatus.COMPLETED.getCode().equals(accessory.getStatus())) {
            FileUtils.deleteDirectory(new File(FileConfig.uploadPath + File.separator + "/upload"
                    + File.separator + accessory.getMd5()));
        }
        sliceService.deleteByMainId(id);
        return baseMapper.deleteByPrimaryKey(id);
    }

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int deleteBatch(Collection<String> ids) {
        List<SysAccessory> accessories = this.selectByIds(ids);
        for (SysAccessory accessory : accessories) {
            if (!FileUploadStatus.COMPLETED.getCode().equals(accessory.getStatus())) {
                FileUtils.deleteDirectory(new File(FileConfig.uploadPath + File.separator + "/upload"
                        + File.separator + accessory.getMd5()));
            }
            sliceService.deleteByMainId(accessory.getId());
        }
        return baseMapper.deleteBatch(ids);
    }
}
