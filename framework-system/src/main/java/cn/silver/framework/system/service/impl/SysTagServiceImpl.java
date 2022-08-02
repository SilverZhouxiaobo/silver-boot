package cn.silver.framework.system.service.impl;

import cn.silver.framework.core.model.TreeSelect;
import cn.silver.framework.core.service.impl.TreeServiceImpl;
import cn.silver.framework.system.domain.SysTag;
import cn.silver.framework.system.mapper.SysTagMapper;
import cn.silver.framework.system.service.ISysTagService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: sys_tag
 * @Author: jeecg-boot
 * @Date: 2021-09-16
 * @Version: V1.0
 */
@Service
public class SysTagServiceImpl extends TreeServiceImpl<SysTagMapper, SysTag> implements ISysTagService {

    @Resource
    private SysTagMapper sysTagMapper;

    @Override
    public List<SysTag> getTagByPid(String id) {
        return sysTagMapper.getTagByPid(id);
    }

    /**
     * 标签扁平化使用
     *
     * @param pid
     * @return
     */
    @Override
    public List<TreeSelect> buildTreeSelect(String pid) {
        List<SysTag> trees;
        if (StringUtils.isNotBlank(pid)) {
            trees = this.selectByPid(pid);
        } else {
            trees = this.selectAll();
//            trees = buildTree(trees);
        }
        return trees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 标签层级使用
     *
     * @param pid
     * @return
     */
    public List<TreeSelect> buildTreeSelectAll(String pid) {
        List<SysTag> trees;
        if (StringUtils.isNotBlank(pid)) {
            trees = this.selectByPid(pid);
        } else {
            trees = this.selectAll();
//            trees = buildTree(trees);
        }
        return trees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    @Override
    public SysTag getTagByCode(String code) {
        return sysTagMapper.getTagByCode(code);
    }

    @Override
    public List<SysTag> getTagByName(String[] name) {
        return sysTagMapper.getTagByName(name);
    }
}
