package cn.silver.framework.system.service.impl;

import cn.silver.framework.core.model.TreeSelect;
import cn.silver.framework.core.service.ITreeService;
import cn.silver.framework.core.service.impl.TreeServiceImpl;
import cn.silver.framework.system.domain.SysCategory;
import cn.silver.framework.system.mapper.SysCategoryMapper;
import cn.silver.framework.system.service.ISysCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: 分类字典
 * @Author: hb
 * @Date: 2019-05-29
 * @Version: V1.0
 */
@Slf4j
@Service
public class SysCategoryServiceImpl extends TreeServiceImpl<SysCategoryMapper, SysCategory> implements ISysCategoryService {

    @Override
    public List<TreeSelect> buildTreeSelect(String pid) {
        List<TreeSelect> result = new ArrayList<>();
        List<SysCategory> trees = this.selectAll();
        Map<String, SysCategory> treeMap = trees.stream().collect(Collectors.toMap(SysCategory::getCode, Function.identity()));
        trees = buildTree(trees);
        if (StringUtils.isBlank(pid)) {
            result = trees.stream().map(TreeSelect::new).collect(Collectors.toList());
        } else {
            List<TreeSelect> finalResult = result;
            treeMap.get(pid).getChildren().forEach(entity -> {
                finalResult.add(new TreeSelect(entity));
            });
        }
        return result;
    }

    @Override
    public List<SysCategory> selectByBaseCode(String baseCode) {
        return this.baseMapper.selectByBaseCode(baseCode);
    }


    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insert(SysCategory category) {
        if (!ITreeService.ROOT_PID_VALUE.equalsIgnoreCase(category.getPid())) {
            SysCategory parent = this.selectById(category.getPid());
            category.setPcode(parent.getCode());
            category.setBaseCode(parent.getBaseCode());
        } else {
            category.setPcode(category.getCode());
            category.setBaseCode(category.getCode());
        }
        return this.baseMapper.insert(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertBatch(Collection<SysCategory> categories) {
        Set<String> pids = categories.stream().filter(category -> StringUtils.isNotBlank(category.getPid())).map(SysCategory::getPid).collect(Collectors.toSet());
        Map<String, SysCategory> parentMap = null;
        if (CollectionUtils.isNotEmpty(pids)) {
            List<SysCategory> parents = this.baseMapper.selectByIds(pids);
            parentMap = CollectionUtils.isNotEmpty(parents) ? parents.stream().collect(Collectors.toMap(SysCategory::getId, Function.identity())) : new HashMap<>();
        }
        for (SysCategory category : categories) {
            if (StringUtils.isNotBlank(category.getPid()) && parentMap.containsKey(category.getPid())) {
                SysCategory parent = parentMap.get(category.getPid());
                category.setPcode(parent.getCode());
                category.setBaseCode(parent.getBaseCode());
            } else {
                category.setPid(ITreeService.ROOT_PID_VALUE);
                category.setPcode(category.getCode());
                category.setBaseCode(category.getCode());
            }
        }
        return this.baseMapper.insertBatch(categories);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int update(SysCategory category) {
        if (!ITreeService.ROOT_PID_VALUE.equalsIgnoreCase(category.getPid())) {
            SysCategory parent = this.selectById(category.getPid());
            category.setPcode(parent.getCode());
            category.setBaseCode(parent.getBaseCode());
        } else {
            category.setPcode(category.getCode());
            category.setBaseCode(category.getCode());
        }
        return this.baseMapper.update(category);
    }
}
