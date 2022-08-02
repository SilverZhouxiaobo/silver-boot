package cn.silver.framework.system.service;

import cn.silver.framework.core.service.IBaseService;
import cn.silver.framework.system.domain.SysAccessorySlice;

import java.util.List;

/**
 * @Description: 附件管理-分片信息
 * @Author: jeecg-boot
 * @Date: 2022-01-04
 * @Version: V1.0
 */
public interface ISysAccessorySliceService extends IBaseService<SysAccessorySlice> {

    List<SysAccessorySlice> selectByMainId(String mainId);

    void deleteByMainId(String id);
}
