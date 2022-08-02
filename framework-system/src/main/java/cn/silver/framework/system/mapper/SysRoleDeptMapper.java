package cn.silver.framework.system.mapper;

import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.system.domain.SysOperLog;
import cn.silver.framework.system.domain.SysRoleDept;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 角色与部门关联表 数据层
 *
 * @author hb
 */
@Mapper
public interface SysRoleDeptMapper extends BaseMapper<SysOperLog> {
    /**
     * 通过角色ID删除角色和部门关联
     *
     * @param roleId 角色ID
     *
     * @return 结果
     */
    int deleteRoleDeptByRoleId(String roleId);

    /**
     * 批量删除角色部门关联信息
     *
     * @param ids 需要删除的数据ID
     *
     * @return 结果
     */
    int deleteRoleDept(Collection<String> ids);

    /**
     * 查询部门使用数量
     *
     * @param deptId 部门ID
     *
     * @return 结果
     */
    int selectCountRoleDeptByDeptId(String deptId);

    /**
     * 批量新增角色部门信息
     *
     * @param roleDeptList 角色部门列表
     *
     * @return 结果
     */
    int batchRoleDept(List<SysRoleDept> roleDeptList);
}
