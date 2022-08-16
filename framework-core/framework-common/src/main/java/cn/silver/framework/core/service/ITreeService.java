package cn.silver.framework.core.service;

import cn.silver.framework.core.domain.TreeEntity;
import cn.silver.framework.core.model.TreeSelect;

import java.util.List;

/**
 * @author Administrator
 */
public interface ITreeService<T extends TreeEntity> extends IBaseService<T> {

    /**
     * 根节点父ID的值
     */
    String ROOT_PID_VALUE = "0";

    List<T> selectByPid(String pid);

    List<T> buildTree();

    List<T> buildTree(List<T> entries);

    List<TreeSelect> buildTreeSelect(String pid);

    List<TreeSelect> buildTreeSelect(List<T> depts);
}
