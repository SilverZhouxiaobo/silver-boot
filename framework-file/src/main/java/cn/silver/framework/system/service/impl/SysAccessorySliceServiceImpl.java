package cn.silver.framework.system.service.impl;

import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.system.domain.SysAccessorySlice;
import cn.silver.framework.system.mapper.SysAccessorySliceMapper;
import cn.silver.framework.system.service.ISysAccessorySliceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 附件管理-分片信息
 * @Author: jeecg-boot
 * @Date: 2022-01-04
 * @Version: V1.0
 */
@Service
public class SysAccessorySliceServiceImpl extends BaseServiceImpl<SysAccessorySliceMapper, SysAccessorySlice> implements ISysAccessorySliceService {

    @Override
    public List<SysAccessorySlice> selectByMainId(String mainId) {
        return this.baseMapper.selectByMainId(mainId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteByMainId(String id) {
        this.baseMapper.deleteByMainId(id);
    }
}
