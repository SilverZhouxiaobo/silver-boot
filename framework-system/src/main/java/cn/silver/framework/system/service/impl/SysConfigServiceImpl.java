package cn.silver.framework.system.service.impl;

import cn.silver.framework.common.constant.Constants;
import cn.silver.framework.common.exception.CustomException;
import cn.silver.framework.common.exception.ServiceException;
import cn.silver.framework.common.utils.StringUtils;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.redis.RedisCache;
import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.core.text.Convert;
import cn.silver.framework.system.constant.UserConstants;
import cn.silver.framework.system.domain.SysConfig;
import cn.silver.framework.system.mapper.SysConfigMapper;
import cn.silver.framework.system.service.ISysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 参数配置 服务层实现
 *
 * @author hb
 */
@Slf4j
@Service
public class SysConfigServiceImpl extends BaseServiceImpl<SysConfigMapper, SysConfig> implements ISysConfigService {

    @Autowired
    private RedisCache redisCache;

    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init() {
        loadingConfigCache();
    }

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    @Cacheable(value = Constants.SYS_CONFIG_KEY, key = "#configKey")
    public String selectConfigByKey(String configKey) {
        SysConfig config = new SysConfig();
        config.setConfigKey(configKey);
        config = this.baseMapper.selectOne(config);
        return ObjectUtils.isNotEmpty(config) ? config.getConfigValue() : StringUtils.EMPTY;
    }

    /**
     * 获取验证码开关
     *
     * @return true开启，false关闭
     */
    @Override
    public boolean selectCaptchaOnOff() {
        String captchaOnOff = selectConfigByKey("sys.account.captchaOnOff");
        if (StringUtils.isEmpty(captchaOnOff)) {
            return true;
        }
        return Convert.toBool(captchaOnOff);
    }

    /**
     * 新增参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    @CachePut(value = Constants.SYS_CONFIG_KEY, key = "#configKey")
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insert(SysConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(this.checkConfigKeyUnique(config))) {
            throw new CustomException("保存失败,参数键名已存在", ResponseEnum.DATA_ERROR_EXIST.getCode());
        }
        int row = super.insert(config);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 修改参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    @CachePut(value = Constants.SYS_CONFIG_KEY, key = "#configKey")
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int update(SysConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(this.checkConfigKeyUnique(config))) {
            throw new CustomException("保存失败,参数键名已存在", ResponseEnum.DATA_ERROR_EXIST.getCode());
        }
        int row = super.update(config);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int deleteBatch(Collection<String> configIds) {
        List<SysConfig> configs = this.baseMapper.selectByIds(configIds);
        List<SysConfig> internals = configs.stream().filter(config -> config.getConfigType()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(internals)) {
            throw new ServiceException(String.format("内置参数【%1$s】不能删除 ", internals.stream().map(SysConfig::getConfigName).collect(Collectors.joining(","))));
        }
        redisCache.deleteObject(configs.stream().map(SysConfig::getConfigKey).collect(Collectors.toList()));
        return baseMapper.deleteBatch(configIds);
    }

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache() {
        List<SysConfig> configsList = this.baseMapper.selectAll();
        for (SysConfig config : configsList) {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache() {
        Collection<String> keys = redisCache.keys(Constants.SYS_CONFIG_KEY + "*");
        redisCache.deleteObject(keys);
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache() {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public String checkConfigKeyUnique(SysConfig config) {
        String configId = StringUtils.isNull(config.getId()) ? "" : config.getId();
        SysConfig info = baseMapper.checkConfigKeyUnique(config.getConfigKey());
        if (StringUtils.isNotNull(info) && !info.getId().equals(configId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public void clearCache() {
        Collection<String> keys = redisCache.keys(Constants.SYS_CONFIG_KEY + "*");
        redisCache.deleteObject(keys);
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey) {
        return Constants.SYS_CONFIG_KEY + configKey;
    }
}
