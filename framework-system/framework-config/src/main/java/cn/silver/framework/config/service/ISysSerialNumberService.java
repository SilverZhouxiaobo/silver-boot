package cn.silver.framework.config.service;


import cn.silver.framework.config.domain.SysSerialNumber;
import cn.silver.framework.core.service.IBaseService;

/**
 * 单编码规则配置Service接口
 *
 * @author hb
 * @date 2022-06-20
 */
public interface ISysSerialNumberService extends IBaseService<SysSerialNumber> {

    String getSerialNumberByBussinessKey(String businessKey);
}
