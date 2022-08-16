package cn.silver.framework.config.service;

import cn.silver.framework.config.domain.SysDictData;
import cn.silver.framework.core.service.IBaseService;

import java.util.Collection;
import java.util.List;

/**
 * 字典 业务层
 *
 * @author hb
 */
public interface ISysDictDataService extends IBaseService<SysDictData> {
    /**
     * 根据条件分页查询字典数据
     *
     * @param dictData 字典数据信息
     *
     * @return 字典数据集合信息
     */
    List<SysDictData> selectDictDataList(SysDictData dictData);

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    String selectDictLabel(String dictType, String dictValue);

    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    SysDictData selectDictDataById(String dictCode);

    List<SysDictData> selectByMainIds(Collection<String> codes);
}
