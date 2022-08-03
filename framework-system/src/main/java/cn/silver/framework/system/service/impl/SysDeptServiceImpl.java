package cn.silver.framework.system.service.impl;

import cn.silver.framework.common.exception.CustomException;
import cn.silver.framework.common.exception.ServiceException;
import cn.silver.framework.common.utils.StringUtils;
import cn.silver.framework.common.utils.spring.SpringUtils;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.domain.TreeEntity;
import cn.silver.framework.core.model.TreeSelect;
import cn.silver.framework.core.service.impl.TreeServiceImpl;
import cn.silver.framework.core.text.Convert;
import cn.silver.framework.db.annotation.DataScope;
import cn.silver.framework.security.util.SecurityUtils;
import cn.silver.framework.system.constant.UserConstants;
import cn.silver.framework.system.domain.SysDept;
import cn.silver.framework.system.domain.SysRole;
import cn.silver.framework.system.domain.SysUser;
import cn.silver.framework.system.mapper.SysDeptMapper;
import cn.silver.framework.system.mapper.SysRoleMapper;
import cn.silver.framework.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门管理 服务实现
 *
 * @author hb
 */
@Service
public class SysDeptServiceImpl extends TreeServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

    @Autowired
    private SysRoleMapper roleMapper;

    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     *
     * @return 部门信息集合
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<SysDept> selectDeptList(SysDept dept) {
        return baseMapper.selectDeptList(dept);
    }

    /**
     * 构建前端所需要树结构
     *
     * @param depts 部门列表
     *
     * @return 树结构列表
     */
    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts) {
        List<SysDept> returnList = new ArrayList<SysDept>();
        List<String> tempList = new ArrayList<String>();
        for (SysDept dept : depts) {
            tempList.add(dept.getId());
        }
        for (SysDept dept : depts) {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getPid())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty()) {
            returnList = depts;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 部门列表
     *
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildDeptTreeSelect(List<SysDept> depts) {
        List<SysDept> deptTrees = buildDeptTree(depts);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     *
     * @return 选中部门列表
     */
    @Override
    public List<String> selectDeptListByRoleId(String roleId) {
        SysRole role = roleMapper.selectRoleById(roleId);
        return baseMapper.selectDeptListByRoleId(roleId, role.isDeptCheckStrictly());
    }

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     *
     * @return 部门信息
     */
    @Override
    public SysDept selectDeptById(String deptId) {
        return baseMapper.selectDeptById(deptId);
    }

    /**
     * 根据ID查询所有子部门（正常状态）
     *
     * @param deptId 部门ID
     *
     * @return 子部门数
     */
    @Override
    public int selectNormalChildrenDeptById(String deptId) {
        return baseMapper.selectNormalChildrenDeptById(deptId);
    }

    /**
     * 是否存在子节点
     *
     * @param deptId 部门ID
     *
     * @return 结果
     */
    @Override
    public boolean hasChildByDeptId(String deptId) {
        int result = baseMapper.hasChildByDeptId(deptId);
        return result > 0;
    }

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     *
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkDeptExistUser(String deptId) {
        int result = baseMapper.checkDeptExistUser(deptId);
        return result > 0;
    }

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     *
     * @return 结果
     */
    @Override
    public String checkDeptNameUnique(SysDept dept) {
        String deptId = StringUtils.isNull(dept.getId()) ? "" : dept.getId();
        SysDept info = baseMapper.checkDeptNameUnique(dept.getDeptName(), dept.getPid());
        if (StringUtils.isNotNull(info) && !info.getId().equals(deptId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验部门是否有数据权限
     *
     * @param deptId 部门id
     */
    @Override
    public void checkDeptDataScope(String deptId) {
        if (!SysUser.isAdmin(SecurityUtils.getUsername())) {
            SysDept dept = new SysDept();
            dept.setId(deptId);
            List<SysDept> depts = SpringUtils.getAopProxy(this).selectDeptList(dept);
            if (StringUtils.isEmpty(depts)) {
                throw new ServiceException("没有权限访问部门数据！");
            }
        }
    }

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     *
     * @return 结果
     */
    @Override
    public int insert(SysDept dept) {
        if (UserConstants.NOT_UNIQUE.equals(this.checkDeptNameUnique(dept))) {
            throw new CustomException(ResponseEnum.DEPARTMENT_ADD_ERROR_EXIST);
        }
        SysDept info = baseMapper.selectDeptById(dept.getPid());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(info.getStatus())) {
            throw new ServiceException("部门停用，不允许新增");
        }
        dept.setAncestors(info.getAncestors() + "," + dept.getPid());
        return super.insert(dept);
    }

    /**
     * 修改保存部门信息
     *
     * @param dept 部门信息
     *
     * @return 结果
     */
    @Override
    public int update(SysDept dept) {
        if (UserConstants.NOT_UNIQUE.equals(this.checkDeptNameUnique(dept))) {
            throw new CustomException(ResponseEnum.DEPARTMENT_UPDATE_ERROR_EXIST);
        } else if (dept.getPid().equals(dept.getId())) {
            throw new CustomException(ResponseEnum.DEPARTMENT_UPDATE_ERROR_SELF);
        } else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus())
                && this.selectNormalChildrenDeptById(dept.getId()) > 0) {
            throw new CustomException(ResponseEnum.DEPARTMENT_UPDATE_ERROR_SUB);
        }
        SysDept newParentDept = baseMapper.selectDeptById(dept.getPid());
        SysDept oldDept = baseMapper.selectDeptById(dept.getId());
        if (StringUtils.isNotNull(newParentDept) && StringUtils.isNotNull(oldDept)) {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getId(), newAncestors, oldAncestors);
        }
        int result = super.update(dept);
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus()) && StringUtils.isNotEmpty(dept.getAncestors())
                && !StringUtils.equals("0", dept.getAncestors())) {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatusNormal(dept);
        }
        return result;
    }

    /**
     * 修改该部门的父级部门状态
     *
     * @param dept 当前部门
     */
    private void updateParentDeptStatusNormal(SysDept dept) {
        String ancestors = dept.getAncestors();
        String[] deptIds = Convert.toStrArray(ancestors);
        baseMapper.updateDeptStatusNormal(deptIds);
    }

    /**
     * 修改子元素关系
     *
     * @param deptId       被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeptChildren(String deptId, String newAncestors, String oldAncestors) {
        List<SysDept> children = baseMapper.selectChildrenDeptById(deptId);
        for (SysDept child : children) {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        if (children.size() > 0) {
            baseMapper.updateDeptChildren(children);
        }
    }

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     *
     * @return 结果
     */
    @Override
    public int delete(String deptId) {
        if (this.hasChildByDeptId(deptId)) {
            throw new CustomException(ResponseEnum.DEPARTMENT_DELETE_ERROR_SUB);
        }
        if (this.checkDeptExistUser(deptId)) {
            throw new CustomException(ResponseEnum.DEPARTMENT_DELETE_ERROR_USER);
        }
        return baseMapper.deleteDeptById(deptId);
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysDept> list, TreeEntity t) {
        // 得到子节点列表
        List<TreeEntity> childList = getChildList(list, t);
        t.setChildren(childList);
        for (TreeEntity tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<TreeEntity> getChildList(List<SysDept> list, TreeEntity t) {
        List<TreeEntity> tlist = new ArrayList<>();
        Iterator<SysDept> it = list.iterator();
        while (it.hasNext()) {
            SysDept n = it.next();
            if (StringUtils.isNotNull(n.getPid()) && n.getPid().equals(t.getId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysDept> list, TreeEntity t) {
        return getChildList(list, t).size() > 0;
    }
}
