package cn.silver.framework.system.service;


import cn.silver.framework.core.service.IBaseService;
import cn.silver.framework.system.domain.SysSerialNumber;

/**
 * 单编码规则配置Service接口
 *
 * @author hb
 * @date 2022-06-20
 */
public interface ISysSerialNumberService extends IBaseService<SysSerialNumber> {

    String getSerialNumberByBussinessKey(String businessKey);
}
