package cn.silver.framework.system.service;

import cn.silver.framework.core.service.ITreeService;
import cn.silver.framework.system.domain.SysTag;

import java.util.List;


public interface ISysTagService extends ITreeService<SysTag> {

    List<SysTag> getTagByPid(String id);

    SysTag getTagByCode(String code);

    List<SysTag> getTagByName(String[] name);

}
