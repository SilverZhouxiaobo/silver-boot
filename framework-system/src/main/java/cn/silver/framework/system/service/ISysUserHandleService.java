package cn.silver.framework.system.service;


import cn.silver.framework.core.service.IBaseService;
import cn.silver.framework.system.domain.SysUserHandle;

/**
 * 个人收藏信息Service接口
 *
 * @author hb
 * @date 2022-07-06
 */
public interface ISysUserHandleService extends IBaseService<SysUserHandle> {

    void handle(String businessType, String businessKey, String handleType, String handleInfo);
}
