package cn.silver.framework.config.service.impl;

import cn.silver.framework.common.utils.DictUtils;
import cn.silver.framework.config.domain.SysDictData;
import cn.silver.framework.config.mapper.SysDictDataMapper;
import cn.silver.framework.config.service.ISysDictDataService;
import cn.silver.framework.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 字典 业务层处理
 *
 * @author hb
 */
@Service
public class SysDictDataServiceImpl extends BaseServiceImpl<SysDictDataMapper, SysDictData> implements ISysDictDataService {

    /**
     * 根据条件分页查询字典数据
     *
     * @param dictData 字典数据信息
     *
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        return baseMapper.selectDictDataList(dictData);
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     *
     * @return 字典标签
     */
    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        return baseMapper.selectDictLabel(dictType, dictValue);
    }

    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     *
     * @return 字典数据
     */
    @Override
    public SysDictData selectDictDataById(String dictCode) {
        return baseMapper.selectDictDataById(dictCode);
    }

    @Override
    public List<SysDictData> selectByMainIds(Collection<String> codes) {
        return baseMapper.selectByMainIds(codes);
    }

    /**
     * 新增保存字典数据信息
     *
     * @param data 字典数据信息
     *
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insert(SysDictData data) {
        int row = super.insert(data);
        List<SysDictData> dictDatas = baseMapper.selectDictDataByType(data.getDictType());
        DictUtils.setDictCache(data.getDictType(), dictDatas.stream().map(SysDictData::getModel).collect(Collectors.toList()));
        return row;
    }

    /**
     * 修改保存字典数据信息
     *
     * @param data 字典数据信息
     *
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int update(SysDictData data) {
        int row = super.update(data);
        List<SysDictData> dictDatas = baseMapper.selectDictDataByType(data.getDictType());
        DictUtils.setDictCache(data.getDictType(), dictDatas.stream().map(SysDictData::getModel).collect(Collectors.toList()));
        return row;
    }

    /**
     * 批量删除字典数据信息
     *
     * @param dataIds 需要删除的字典数据ID
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int deleteBatch(Collection<String> dataIds) {
        List<SysDictData> dataList = this.selectByIds(dataIds);
        int result = super.deleteBatch(dataIds);
        Set<String> codes = dataList.stream().map(SysDictData::getDictType).collect(Collectors.toSet());
        dataList = this.selectByMainIds(codes);
        dataList.stream().collect(Collectors.groupingBy(SysDictData::getDictType)).entrySet().stream().forEach(entry -> {
            DictUtils.setDictCache(entry.getKey(), entry.getValue().stream().map(SysDictData::getModel).collect(Collectors.toList()));
        });
        return result;
    }
}
