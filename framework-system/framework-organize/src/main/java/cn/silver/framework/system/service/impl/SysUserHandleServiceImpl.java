package cn.silver.framework.system.service.impl;


import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.system.constant.HandleType;
import cn.silver.framework.system.domain.SysComment;
import cn.silver.framework.system.domain.SysUserHandle;
import cn.silver.framework.system.mapper.SysUserHandleMapper;
import cn.silver.framework.system.service.ISysCommentService;
import cn.silver.framework.system.service.ISysUserHandleService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 个人收藏信息Service业务层处理
 *
 * @author hb
 * @date 2022-07-06
 */
@Service
public class SysUserHandleServiceImpl extends BaseServiceImpl<SysUserHandleMapper, SysUserHandle> implements ISysUserHandleService {
    @Autowired
    private ISysCommentService commentService;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void handle(String businessType, String businessKey, String handleType, String handleInfo) {
        SysUserHandle handle = new SysUserHandle(handleType, businessType, businessKey);
        SysUserHandle exists = this.baseMapper.selectOne(handle);
        switch (HandleType.getType(handleType)) {
            case HANDLE_COMMENT:
                commentService.insert(new SysComment(handleInfo));
                break;
            case HANDLE_LIKE_ON:
            case HANDLE_FOCUS_ON:
            case HANDLE_COLLECT_ON:
                if (ObjectUtils.isEmpty(exists)) {
                    this.insert(handle);
                }
                break;
            case HANDLE_COLLECT_OFF:
                if (ObjectUtils.isNotEmpty(exists)) {
                    this.delete(exists.getId());
                }
                break;
            default:
                break;
        }
    }
}
