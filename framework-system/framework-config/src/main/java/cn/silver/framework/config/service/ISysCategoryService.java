package cn.silver.framework.config.service;

import cn.silver.framework.config.domain.SysCategory;
import cn.silver.framework.core.service.ITreeService;

import java.util.List;

/**
 * @Description: 分类字典
 * @Author: hb
 * @Date: 2019-05-29
 * @Version: V1.0
 */
public interface ISysCategoryService extends ITreeService<SysCategory> {

    List<SysCategory> selectByBaseCode(String baseCode);
}
