package cn.silver.framework.config.service;

import cn.silver.framework.config.domain.SysTag;
import cn.silver.framework.core.service.ITreeService;

import java.util.List;


public interface ISysTagService extends ITreeService<SysTag> {

    List<SysTag> getTagByPid(String id);

    SysTag getTagByCode(String code);

    List<SysTag> getTagByName(String[] name);

}
