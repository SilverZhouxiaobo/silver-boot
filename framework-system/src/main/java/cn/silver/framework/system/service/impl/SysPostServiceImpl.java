package cn.silver.framework.system.service.impl;

import cn.silver.framework.common.exception.CustomException;
import cn.silver.framework.common.exception.ServiceException;
import cn.silver.framework.common.utils.StringUtils;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.system.constant.UserConstants;
import cn.silver.framework.system.domain.SysPost;
import cn.silver.framework.system.mapper.SysPostMapper;
import cn.silver.framework.system.mapper.SysUserPostMapper;
import cn.silver.framework.system.service.ISysPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * 岗位信息 服务层处理
 *
 * @author hb
 */
@Service
public class SysPostServiceImpl extends BaseServiceImpl<SysPostMapper, SysPost> implements ISysPostService {
    @Autowired
    private SysUserPostMapper userPostMapper;

    /**
     * 查询岗位信息集合
     *
     * @param post 岗位信息
     *
     * @return 岗位信息集合
     */
    @Override
    public List<SysPost> selectPostList(SysPost post) {
        return baseMapper.selectPostList(post);
    }

    /**
     * 查询所有岗位
     *
     * @return 岗位列表
     */
    @Override
    public List<SysPost> selectPostAll() {
        return baseMapper.selectPostAll();
    }

    /**
     * 通过岗位ID查询岗位信息
     *
     * @param postId 岗位ID
     *
     * @return 角色对象信息
     */
    @Override
    public SysPost selectPostById(String postId) {
        return baseMapper.selectPostById(postId);
    }

    /**
     * 根据用户ID获取岗位选择框列表
     *
     * @param userId 用户ID
     *
     * @return 选中岗位ID列表
     */
    @Override
    public List<String> selectPostListByUserId(String userId) {
        return baseMapper.selectPostListByUserId(userId);
    }

    /**
     * 校验岗位名称是否唯一
     *
     * @param post 岗位信息
     *
     * @return 结果
     */
    @Override
    public String checkPostNameUnique(SysPost post) {
        String postId = StringUtils.isNull(post.getId()) ? "" : post.getId();
        SysPost info = baseMapper.checkPostNameUnique(post.getPostName());
        if (StringUtils.isNotNull(info) && !info.getId().equals(postId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验岗位编码是否唯一
     *
     * @param post 岗位信息
     *
     * @return 结果
     */
    @Override
    public String checkPostCodeUnique(SysPost post) {
        String postId = StringUtils.isNull(post.getId()) ? "" : post.getId();
        SysPost info = baseMapper.checkPostCodeUnique(post.getPostCode());
        if (StringUtils.isNotNull(info) && !info.getId().equals(postId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 通过岗位ID查询岗位使用数量
     *
     * @param postId 岗位ID
     *
     * @return 结果
     */
    @Override
    public int countUserPostById(String postId) {
        return userPostMapper.countUserPostById(postId);
    }

    /**
     * 删除岗位信息
     *
     * @param postId 岗位ID
     *
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int delete(String postId) {
        if (countUserPostById(postId) > 0) {
            SysPost post = selectPostById(postId);
            throw new ServiceException(String.format("%1$s已分配,不能删除", post.getPostName()));
        }
        return super.delete(postId);
    }

    /**
     * 批量删除岗位信息
     *
     * @param postIds 需要删除的岗位ID
     *
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int deleteBatch(Collection<String> postIds) {
        for (String postId : postIds) {
            if (countUserPostById(postId) > 0) {
                SysPost post = selectPostById(postId);
                throw new ServiceException(String.format("%1$s已分配,不能删除", post.getPostName()));
            }
        }
        return super.deleteBatch(postIds);
    }

    /**
     * 新增保存岗位信息
     *
     * @param post 岗位信息
     *
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insert(SysPost post) {
        if (UserConstants.NOT_UNIQUE.equals(this.checkPostNameUnique(post))) {
            throw new CustomException(ResponseEnum.POST_ADD_ERROR_EXIST_NAME);
        } else if (UserConstants.NOT_UNIQUE.equals(this.checkPostCodeUnique(post))) {
            throw new CustomException(ResponseEnum.POST_ADD_ERROR_EXIST_CODE);
        }
        return super.insert(post);
    }

    /**
     * 修改保存岗位信息
     *
     * @param post 岗位信息
     *
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int update(SysPost post) {
        if (UserConstants.NOT_UNIQUE.equals(this.checkPostNameUnique(post))) {
            throw new CustomException(ResponseEnum.POST_UPDATE_ERROR_EXIST_NAME);
        } else if (UserConstants.NOT_UNIQUE.equals(this.checkPostCodeUnique(post))) {
            throw new CustomException(ResponseEnum.POST_UPDATE_ERROR_EXIST_CODE);
        }
        return super.update(post);
    }
}
