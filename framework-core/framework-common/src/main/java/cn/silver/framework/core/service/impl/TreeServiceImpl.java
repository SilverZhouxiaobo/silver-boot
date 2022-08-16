package cn.silver.framework.core.service.impl;

import cn.silver.framework.core.domain.TreeEntity;
import cn.silver.framework.core.mapper.TreeMapper;
import cn.silver.framework.core.model.TreeSelect;
import cn.silver.framework.core.service.ITreeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Slf4j
public class TreeServiceImpl<M extends TreeMapper<T>, T extends TreeEntity> extends BaseServiceImpl<M, T> implements ITreeService<T> {

    @Override
    public List<T> selectByPid(String pid) {
        return this.baseMapper.selectByPid(pid);
    }

    @Override
    public List<T> buildTree() {
        return this.buildTree(this.selectAll());
    }

    @Override
    public List<T> buildTree(List<T> entries) {
        List<T> returnList = new ArrayList<T>();
        List<String> tempList = new ArrayList<>();
        for (T entity : entries) {
            tempList.add(entity.getId());
        }
        for (Iterator<T> iterator = entries.iterator(); iterator.hasNext(); ) {
            T entity = iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(entity.getPid())) {
                recursionFn(entries, entity);
                returnList.add(entity);
            }
        }
        if (returnList.isEmpty()) {
            returnList = entries;
        }
        return returnList;
    }

    @Override
    public List<TreeSelect> buildTreeSelect(String pid) {
        List<TreeSelect> result = new ArrayList<>();
        List<T> trees = this.selectAll();
        Map<String, T> treeMap = trees.stream().collect(Collectors.toMap(TreeEntity::getId, Function.identity()));
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
    public List<TreeSelect> buildTreeSelect(List<T> entities) {
        List<T> trees = buildTree(entities);
        return trees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insert(T entity) {
        if (StringUtils.isBlank(entity.getPid())) {
            entity.setPid(ITreeService.ROOT_PID_VALUE);
        }
        return this.baseMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertBatch(Collection<T> entities) {
        for (T entity : entities) {
            if (StringUtils.isBlank(entity.getPid())) {
                entity.setPid(ITreeService.ROOT_PID_VALUE);
            }
        }
        return this.baseMapper.insertBatch(entities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int update(T entity) {
        if (StringUtils.isBlank(entity.getPid())) {
            entity.setPid(ITreeService.ROOT_PID_VALUE);
        }
        return this.baseMapper.update(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int updateBatch(Collection<T> entities) {
        for (T entity : entities) {
            if (StringUtils.isBlank(entity.getPid())) {
                entity.setPid(ITreeService.ROOT_PID_VALUE);
            }
        }
        return super.updateBatch(entities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertOrUpdate(T entity) {
        if (StringUtils.isBlank(entity.getPid())) {
            entity.setPid(ITreeService.ROOT_PID_VALUE);
        }
        return super.insertOrUpdate(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertOrUpdateBatch(Collection<T> entities) {
        for (T entity : entities) {
            if (StringUtils.isBlank(entity.getPid())) {
                entity.setPid(ITreeService.ROOT_PID_VALUE);
            }
        }
        return super.insertOrUpdateBatch(entities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int delete(String id) {
        List<String> ids = this.getChildrenId(Arrays.asList(id));
        return this.baseMapper.deleteBatch(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int deleteBatch(Collection<String> ids) {
        ids = this.getChildrenId(ids);
        return this.baseMapper.deleteBatch(ids);
    }

    private void recursionFn(List<T> list, T t) {
        // 得到子节点列表
        List<T> childList = getChildList(list, t);
        t.setChildren((List<TreeEntity>) childList);
        for (T tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<T> list, T t) {
        return getChildList(list, t).size() > 0;
    }

    /**
     * 递归列表
     *
     * @param list
     * @param t
     */
    private List<T> getChildList(List<T> list, T t) {
        List<T> tlist = new ArrayList<>();
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            T n = it.next();
            if (n.getPid().equals(t.getId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    public List<String> getChildrenId(Collection<String> ids) {
        List<String> result = new ArrayList<>();
        List<T> entities = this.getChildren(ids);
        result.addAll(ids);
        while (CollectionUtils.isNotEmpty(entities)) {
            ids = entities.stream().map(TreeEntity::getId).collect(Collectors.toList());
            result.addAll(ids);
            entities = this.getChildren(ids);
        }
        return result;
    }

    public List<T> getChildren(Collection<String> pids) {
        return this.baseMapper.selectByPids(pids);
    }
}
