package cn.silver.framework.system.api.service;

import cn.silver.framework.core.api.ISysBaseApi;
import cn.silver.framework.core.bean.LoginLog;
import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.core.bean.OperLog;
import cn.silver.framework.core.model.DictModel;
import cn.silver.framework.core.model.ExternalLinkModel;
import cn.silver.framework.system.domain.*;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysBaseImpl extends SystemServiceFactory implements ISysBaseApi {
    @Override
    public void insertLogininfor(LoginLog loginLog) {
        this.logininforService.insertLogininfor(new SysLogininfor(loginLog));
    }

    @Override
    public void insertOperlog(OperLog operLog) {
        this.operLogService.insertOperlog(new SysOperLog(operLog));
    }

    @Override
    public List<DictModel> selectDictByType(String type) {
        List<SysDictData> datas = this.dictTypeService.selectDictDataByType(type);
        return datas.stream().map(SysDictData::getModel).collect(Collectors.toList());
    }

    @Override
    public String selectCategoryByCodes(String area) {
        return this.categoryService.selectByValue(area).getName();
    }

    @Override
    public void produceLinkBatch(List<ExternalLinkModel> models) {
        List<SysExternalLink> links = models.stream().map(SysExternalLink::new).collect(Collectors.toList());
        this.linkService.insertBatch(links);
    }

    @Override
    public String getCode(String businessKey) {
        return this.numberService.getSerialNumberByBussinessKey(businessKey);
    }

    @Override
    public String selectTagByNames() {
        List<SysTag> sysTags = iSysTagService.selectAll();
        Map<String, String> tagMap = sysTags.stream().collect(Collectors.toMap(SysTag::getName, SysTag::getCode, (key1, key2) -> key1));
        return JSON.toJSONString(tagMap);
    }

    @Override
    public String getConfigByCode(String key) {
        return configService.selectConfigByKey(key);
    }

    @Override
    public void handle(String businessType, String businessKey, String handleType, String handleInfo) {
        this.handleService.handle(businessType, businessKey, handleType, handleInfo);
    }

    @Override
    public List<LoginUser> selectUserByIds(Collection<String> userIds) {
        List<LoginUser> result = new ArrayList<>();
        userIds = userIds.stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(userIds)) {
            List<SysUser> users = userService.selectByIds(userIds);
            result = users.stream().map(SysUser::getLoginUser).collect(Collectors.toList());
        }
        return result;
    }
}
