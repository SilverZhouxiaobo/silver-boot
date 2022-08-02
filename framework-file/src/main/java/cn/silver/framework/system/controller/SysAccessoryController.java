package cn.silver.framework.system.controller;

import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.file.config.FileConfig;
import cn.silver.framework.file.constant.FileUploadType;
import cn.silver.framework.file.util.CommonUtils;
import cn.silver.framework.file.util.FileUtils;
import cn.silver.framework.monitor.annotation.Log;
import cn.silver.framework.monitor.constant.BusinessType;
import cn.silver.framework.system.domain.SysAccessory;
import cn.silver.framework.system.domain.SysAccessorySlice;
import cn.silver.framework.system.service.ISysAccessoryService;
import cn.silver.framework.system.service.ISysAccessorySliceService;
import cn.silver.framework.system.vo.SysAccessoryConvert;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 附件管理
 * @Author: jeecg-boot
 * @Date: 2022-01-04
 * @Version: V1.0
 */
@Slf4j
@RestController
@Api(tags = "附件管理")
@RequestMapping("/sys/accessory")
public class SysAccessoryController extends DataController<ISysAccessoryService, SysAccessory> {
    @Autowired
    private ISysAccessorySliceService sliceService;

    @Override
    @PostMapping("/save")
    @ApiOperation(value = "附件管理-保存附件信息", notes = "附件管理-保存附件信息")
    @Log(title = "附件管理-保存附件信息", businessType = BusinessType.INSERT)
    public Response<SysAccessory> save(@Validated @RequestBody SysAccessory accessory) {
        /**
         * 根据提交的信息查询已经上传的文件，判断是否存在未传输完成需要继续上传的
         * 查询条件：文件md5，文件名，保存模式（本地文件还是minio），附件类型（归属系统），业务主键，
         */
        List<SysAccessory> exist = this.baseService.selectExists(accessory);
        if (CollectionUtils.isEmpty(exist)) {
            accessory.setStatus("01");
            accessory.setSliceFlag(accessory.getSliceNum() > 1);
            accessory.setSaveMode(FileConfig.uploadType);
            String name = accessory.getName();
            accessory.setBaseName(FilenameUtils.getBaseName(name));
            name = name.replace(accessory.getBaseName(), accessory.getBaseName() + "_" + System.currentTimeMillis());
            accessory.setLocation(accessory.getBizPath() + "/" + name);
            if (accessory.isSliceFlag() && !FileUploadType.LOCAL.getCode().equals(FileConfig.uploadType)) {
                String uploadId = CommonUtils.getUploadId(FileConfig.uploadType, accessory.getLocation(), accessory.getFormat());
                accessory.setUploadId(uploadId);
            }
            this.baseService.insert(accessory);
            accessory.setSuccessChunk(0);
        } else {
            accessory = exist.get(0);
            List<SysAccessorySlice> slices = this.sliceService.selectByMainId(accessory.getId());
            accessory.setSliceFlag(accessory.getSliceNum() > 1);
            accessory.setSuccessChunk(slices.size());
            accessory.setStatus("02");
        }
        return Response.success("附件信息保存成功", accessory);
    }

    @PostMapping("/upload")
    @ApiOperation(value = "附件管理-文件上传", notes = "附件管理-文件上传")
    @Log(title = "附件管理-文件上传", businessType = BusinessType.INSERT)
    public Response<SysAccessory> upload(@RequestParam(value = "file") MultipartFile file, @RequestParam(value = "id") String id) {
        SysAccessory accessory = this.baseService.selectById(id);
        try {
            String sign = FileUtils.getMd5(file.getInputStream());
            String path = "";
            String bizPath = accessory.getLocation();
            if (FileUploadType.LOCAL.getCode().equals(accessory.getSaveMode())) {
                path = CommonUtils.uploadLocal(file, bizPath, accessory.getSaveMode());
            } else {
                path = CommonUtils.upload(file.getInputStream(), accessory.getFormat(), bizPath, accessory.getSaveMode());
            }
            accessory.setFileUrl(path);
            accessory.setStatus("03");
            this.baseService.update(accessory);
            return Response.success("文件上传成功，保存路径为：" + path, accessory);
        } catch (Exception e) {
            log.error("文件上传失败，" + e.getMessage(), e);
            return Response.error("文件上传失败，" + e.getMessage());
        }
    }

    @PostMapping("/uploadPart")
    @Log(title = "附件管理-文件分片上传", businessType = BusinessType.INSERT)
    @ApiOperation(value = "附件管理-文件分片上传", notes = "附件管理-文件分片上传")
    public Response<Void> uploadPart(@RequestParam(value = "file") MultipartFile file, @RequestParam(value = "chunk") Integer chunk,
                                     @RequestParam(value = "id") String id) {
        SysAccessory accessory = this.baseService.selectById(id);
        String location = "";
        if (FileUploadType.LOCAL.getCode().equals(accessory.getSaveMode())) {
            String bizPath = accessory.getLocation().substring(0, accessory.getLocation().indexOf(accessory.getBaseName()));
            location = CommonUtils.uploadPartLocal(bizPath, file, accessory.getMd5(), chunk);
        } else {
            location = CommonUtils.uploadPart(accessory.getSaveMode(), accessory.getLocation(), file, accessory.getUploadId(), chunk);
        }
        SysAccessorySlice slice = SysAccessoryConvert.getSlice(accessory, chunk, location);
        this.sliceService.insert(slice);
        return Response.success("文件分片上传成功", null);
    }

    @PostMapping("/merge")
    @Log(title = "附件管理-文件合并", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "附件管理-文件合并", notes = "附件管理-文件合并")
    public Response<SysAccessory> merge(@RequestBody JSONObject params) {
        String id = params.getString("id");
        SysAccessory accessory = this.baseService.selectById(id);
        try {
            List<SysAccessorySlice> slices = this.sliceService.selectByMainId(id);
            String path = "";
            String sign = null;
            if (FileUploadType.LOCAL.getCode().equals(accessory.getSaveMode())) {
                String[] parts = slices.stream().map(SysAccessorySlice::getLocation).collect(Collectors.toList()).toArray(new String[]{});
                path = CommonUtils.mergeFileLocal(accessory.getLocation(), parts);
                sign = FileUtils.getMd5(new File(path));
            } else {
                List<String> parts = slices.stream().map(SysAccessorySlice::getLocation).collect(Collectors.toList());
                path = CommonUtils.mergeFile(accessory.getSaveMode(), accessory.getLocation(), accessory.getUploadId(), parts);
                sign = CommonUtils.getSign(accessory.getSaveMode(), accessory.getLocation());
            }
            accessory.setFileUrl(path);
            accessory.setSign(sign);
            accessory.setStatus("03");
            this.baseService.update(accessory);
            this.sliceService.deleteByMainId(accessory.getId());
            return Response.success("文件合并成功，保存路径为：" + path, accessory);
        } catch (Exception e) {
            log.error("文件合并失败，" + e.getMessage(), e);
            return Response.error("文件合并失败，" + e.getMessage());
        }
    }

}
