package cn.silver.framework.system.service.impl;


import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.system.domain.SysSerialNumber;
import cn.silver.framework.system.mapper.SysSerialNumberMapper;
import cn.silver.framework.system.service.ISysSerialNumberService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 单编码规则配置Service业务层处理
 *
 * @author hb
 * @date 2022-06-20
 */
@Service
public class SysSerialNumberServiceImpl extends BaseServiceImpl<SysSerialNumberMapper, SysSerialNumber> implements ISysSerialNumberService {

//    /**
//     * 生成器锁
//     */
//    private final ReentrantLock lock = new ReentrantLock();
//    /**
//     * 预生成锁
//     */
//    private final ReentrantLock prepareLock = new ReentrantLock();
//    /**
//     * 预生成流水号
//     */
//    HashMap<String, List<String>> prepareSerialNumberMap = new HashMap<>();

    /**
     * 根据模块code生成预数量的序列号存放到Map中
     *
     * @param businessCode 业务编码
     *
     * @return
     */
//    @CachePut(value = "serialNumber", key = "#businessCode")
    public List<String> generatePrepareSerialNumbers(String businessCode) {
        SysSerialNumber serialNumber = this.baseMapper.selectByBusinessCode(businessCode);
        Integer prepare = serialNumber.getPreMaxNum();
        //临时List变量
        List<String> resultList = new ArrayList<>(serialNumber.getPreMaxNum());
//        lock.lock();
//        try {
        int maxSerial = ObjectUtils.isEmpty(serialNumber.getLastGetTime()) || !DateFormatUtils.format(new Date(), serialNumber.getDateFormatStr())
                .equals(DateFormatUtils.format(serialNumber.getLastGetTime(), serialNumber.getDateFormatStr())) ? 0 : serialNumber.getMaxSerial();
        DecimalFormat format = new DecimalFormat(serialNumber.getNumberExpression());
        for (int i = 0; i < prepare; i++) {
            maxSerial++;
            //动态数字生成
            String formatSerialNum = serialNumber.getPrefix() + DateFormatUtils.format(new Date(), serialNumber.getDateFormatStr()) + format.format(maxSerial);
            resultList.add(formatSerialNum);
        }
        //更新数据
        serialNumber.setMaxSerial(maxSerial);
        serialNumber.setLastGetTime(new Date());
        this.baseMapper.update(serialNumber);
//        } finally {
//            lock.unlock();
//        }
        return resultList;
    }

    /**
     * 根据模块code生成序列号
     *
     * @param businessKey 模块code
     *
     * @return 序列号
     */
    @Override
    public String getSerialNumberByBussinessKey(String businessKey) {
        SysSerialNumber serialNumber = this.baseMapper.selectByBusinessCode(businessKey);
        int maxSerial = ObjectUtils.isEmpty(serialNumber.getLastGetTime()) || !DateFormatUtils.format(new Date(), serialNumber.getDateFormatStr())
                .equals(DateFormatUtils.format(serialNumber.getLastGetTime(), serialNumber.getDateFormatStr())) ? 0 : serialNumber.getMaxSerial();
        DecimalFormat format = new DecimalFormat(serialNumber.getNumberExpression());
        maxSerial++;
        //动态数字生成
        String formatSerialNum = serialNumber.getPrefix() + DateFormatUtils.format(new Date(), serialNumber.getDateFormatStr()) + format.format(maxSerial);
        //更新数据
        serialNumber.setMaxSerial(maxSerial);
        serialNumber.setLastGetTime(new Date());
        this.baseMapper.update(serialNumber);
        return formatSerialNum;
//        if (ObjectUtils.isEmpty(serialNumber.getLastGetTime()) || DateFormatUtils.format(new Date(), serialNumber.getDateFormatStr())
//                .equals(DateFormatUtils.format(serialNumber.getLastGetTime(), serialNumber.getDateFormatStr()))) {
//            prepareSerialNumberMap.clear();
//        }
//        //预序列号加锁
//        prepareLock.lock();
//        try {
//            //判断内存中是否还有序列号
//            if (CollectionUtils.isNotEmpty(prepareSerialNumberMap.get(businessKey))) {
//                //若有，返回第一个，并删除
//                return prepareSerialNumberMap.get(businessKey).remove(0);
//            }
//        } finally {
//            //预序列号解锁
//            prepareLock.unlock();
//        }
//        //生成预序列号，存到缓存中
//        List<String> resultList = generatePrepareSerialNumbers(businessKey);
//        prepareLock.lock();
//        try {
//            prepareSerialNumberMap.put(businessKey, resultList);
//            return prepareSerialNumberMap.get(businessKey).remove(0);
//        } finally {
//            prepareLock.unlock();
//        }
    }
}
