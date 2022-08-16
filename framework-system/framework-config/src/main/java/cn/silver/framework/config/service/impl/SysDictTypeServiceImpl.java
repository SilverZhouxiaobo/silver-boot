package cn.silver.framework.config.service.impl;

import cn.silver.framework.common.constant.Constants;
import cn.silver.framework.common.exception.ServiceException;
import cn.silver.framework.common.utils.DictUtils;
import cn.silver.framework.common.utils.StringUtils;
import cn.silver.framework.config.domain.SysDictType;
import cn.silver.framework.config.mapper.SysDictDataMapper;
import cn.silver.framework.config.mapper.SysDictTypeMapper;
import cn.silver.framework.config.service.ISysDictTypeService;
import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.system.constant.UserConstants;
import cn.silver.framework.system.domain.SysDictData;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典 业务层处理
 *
 * @author hb
 */
@Service
public class SysDictTypeServiceImpl extends BaseServiceImpl<SysDictTypeMapper, SysDictType> implements ISysDictTypeService {
    @Resource
    private SysDictTypeMapper dictTypeMapper;

    @Resource
    private SysDictDataMapper dictDataMapper;

    /**
     * 项目启动时，初始化字典到缓存
     */
    @PostConstruct
    public void init() {
        loadingDictCache();
    }

    /**
     * 根据条件分页查询字典类型
     *
     * @param dictType 字典类型信息
     *
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType) {
        return dictTypeMapper.selectDictTypeList(dictType);
    }

    /**
     * 根据所有字典类型
     *
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeAll() {
        return dictTypeMapper.selectDictTypeAll();
    }

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     *
     * @return 字典数据集合信息
     */
    @Override
    @Cacheable(value = Constants.SYS_DICT_KEY, key = "#dictType")
    public List<SysDictData> selectDictDataByType(String dictType) {
//        List<SysDictData> dictDatas = null;
//        List<DictModel> models = DictUtils.getDictCache(dictType);
//        if (StringUtils.isNotEmpty(models)) {
//            dictDatas = models.stream().map(model -> new SysDictData(model)).collect(Collectors.toList());
//            return dictDatas;
//        }
//        dictDatas = dictDataMapper.selectDictDataByType(dictType);
//        if (StringUtils.isNotEmpty(dictDatas)) {
//            models = dictDatas.stream().map(SysDictData::getModel).collect(Collectors.toList());
//            DictUtils.setDictCache(dictType, models);
//            return dictDatas;
//        }
        return dictDataMapper.selectDictDataByType(dictType);
    }

    /**
     * 根据字典类型ID查询信息
     *
     * @param dictId 字典类型ID
     *
     * @return 字典类型
     */
    @Override
    public SysDictType selectDictTypeById(String dictId) {
        return dictTypeMapper.selectDictTypeById(dictId);
    }

    /**
     * 根据字典类型查询信息
     *
     * @param dictType 字典类型
     *
     * @return 字典类型
     */
    @Override
    public SysDictType selectDictTypeByType(String dictType) {
        return dictTypeMapper.selectDictTypeByType(dictType);
    }

    /**
     * 批量删除字典类型信息
     *
     * @param dictIds 需要删除的字典ID
     *
     * @return
     */
    @Override
    public int deleteDictTypeByIds(String[] dictIds) {
        for (String dictId : dictIds) {
            SysDictType dictType = selectDictTypeById(dictId);
            if (dictDataMapper.countDictDataByType(dictType.getDictType()) > 0) {
                throw new ServiceException(String.format("%1$s已分配,不能删除", dictType.getDictName()));
            }
            dictTypeMapper.deleteDictTypeById(dictId);
            DictUtils.removeDictCache(dictType.getDictType());
        }
        return 0;
    }

    /**
     * 加载字典缓存数据
     */
    @Override
    public void loadingDictCache() {
        SysDictData dictData = new SysDictData();
        dictData.setStatus("0");
        Map<String, List<SysDictData>> dictDataMap = dictDataMapper.selectDictDataList(dictData).stream().collect(Collectors.groupingBy(SysDictData::getDictType));
        for (Map.Entry<String, List<SysDictData>> entry : dictDataMap.entrySet()) {
            DictUtils.setDictCache(entry.getKey(), entry.getValue().stream().sorted(Comparator.comparing(SysDictData::getDictSort)).map(SysDictData::getModel).collect(Collectors.toList()));
        }
    }

    /**
     * 清空字典缓存数据
     */
    @Override
    public void clearDictCache() {
        DictUtils.clearDictCache();
    }

    /**
     * 重置字典缓存数据
     */
    @Override
    public void resetDictCache() {
        clearDictCache();
        loadingDictCache();
    }

    /**
     * 新增保存字典类型信息
     *
     * @param dict 字典类型信息
     *
     * @return 结果
     */
    @Override
    public int insertDictType(SysDictType dict) {
        int row = dictTypeMapper.insertDictType(dict);
        if (row > 0) {
            DictUtils.setDictCache(dict.getDictType(), null);
        }
        return row;
    }

    /**
     * 修改保存字典类型信息
     *
     * @param dict 字典类型信息
     *
     * @return 结果
     */
    @Override
    @Transactional
    public int updateDictType(SysDictType dict) {
        SysDictType oldDict = dictTypeMapper.selectDictTypeById(dict.getId());
        dictDataMapper.updateDictDataType(oldDict.getDictType(), dict.getDictType());
        int row = dictTypeMapper.updateDictType(dict);
        if (row > 0) {
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(dict.getDictType());
            DictUtils.setDictCache(dict.getDictType(), dictDatas.stream().map(SysDictData::getModel).collect(Collectors.toList()));
        }
        return row;
    }

    /**
     * 校验字典类型称是否唯一
     *
     * @param dict 字典类型
     *
     * @return 结果
     */
    @Override
    public String checkDictTypeUnique(SysDictType dict) {
        String dictId = StringUtils.isNull(dict.getId()) ? "" : dict.getId();
        SysDictType dictType = dictTypeMapper.checkDictTypeUnique(dict.getDictType());
        if (StringUtils.isNotNull(dictType) && !dictType.getId().equals(dictId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public void clearCache() {
        DictUtils.clearDictCache();
    }
}
