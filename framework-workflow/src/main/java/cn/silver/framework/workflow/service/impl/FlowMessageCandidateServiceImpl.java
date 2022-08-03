package cn.silver.framework.workflow.service.impl;

import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.workflow.constant.FlowConstant;
import cn.silver.framework.workflow.domain.FlowMessageCandidate;
import cn.silver.framework.workflow.domain.FlowTaskExt;
import cn.silver.framework.workflow.mapper.FlowMessageCandidateMapper;
import cn.silver.framework.workflow.object.FlowTaskPostCandidateGroup;
import cn.silver.framework.workflow.service.IFlowApiService;
import cn.silver.framework.workflow.service.IFlowMessageCandidateService;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class FlowMessageCandidateServiceImpl extends BaseServiceImpl<FlowMessageCandidateMapper, FlowMessageCandidate> implements IFlowMessageCandidateService {

    @Autowired
    private IFlowApiService flowApiService;


    @Override
    public boolean isCandidateIdentityOnMessage(String messageId) {
        FlowMessageCandidate entity = new FlowMessageCandidate();
        entity.setMessageId(messageId);
        entity.setCandidateIds(flowApiService.buildGroupIdSet());
        return this.baseMapper.selectCount(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void saveBatch(String messageId, String candidateType, String candidateIds) {
        if (StringUtils.isNotBlank(candidateIds)) {
            Arrays.asList(candidateIds.split(",")).forEach(candidateId -> this.insert(new FlowMessageCandidate(messageId, candidateType, candidateId)));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void saveBatch(String instanceId, FlowTaskExt flowTaskExt, String messageId) {
        this.saveBatch(
                messageId, FlowConstant.GROUP_TYPE_USER_VAR, flowTaskExt.getCandidateUsernames());
        this.saveBatch(
                messageId, FlowConstant.GROUP_TYPE_ROLE_VAR, flowTaskExt.getRoleIds());
        this.saveBatch(
                messageId, FlowConstant.GROUP_TYPE_DEPT_VAR, flowTaskExt.getDeptIds());
        if (StrUtil.equals(flowTaskExt.getGroupType(), FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER)) {
            Object v = flowApiService.getProcessInstanceVariable(
                    instanceId, FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER_VAR);
            if (v != null) {
                this.saveBatch(
                        messageId, FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER_VAR, v.toString());
            }
        } else if (StrUtil.equals(flowTaskExt.getGroupType(), FlowConstant.GROUP_TYPE_DEPT_POST_LEADER)) {
            Object v = flowApiService.getProcessInstanceVariable(
                    instanceId, FlowConstant.GROUP_TYPE_DEPT_POST_LEADER_VAR);
            if (v != null) {
                this.saveBatch(
                        messageId, FlowConstant.GROUP_TYPE_DEPT_POST_LEADER_VAR, v.toString());
            }
        } else if (StrUtil.equals(flowTaskExt.getGroupType(), FlowConstant.GROUP_TYPE_POST)) {
            Assert.notBlank(flowTaskExt.getDeptPostListJson());
            List<FlowTaskPostCandidateGroup> groupDataList =
                    JSONArray.parseArray(flowTaskExt.getDeptPostListJson(), FlowTaskPostCandidateGroup.class);
            for (FlowTaskPostCandidateGroup groupData : groupDataList) {
                FlowMessageCandidate candidateIdentity = new FlowMessageCandidate();
                candidateIdentity.preInsert();
                candidateIdentity.setMessageId(messageId);
                candidateIdentity.setCandidateType(groupData.getType());
                switch (groupData.getType()) {
                    case FlowConstant.GROUP_TYPE_ALL_DEPT_POST_VAR:
                        candidateIdentity.setCandidateId(groupData.getPostId());
                        insert(candidateIdentity);
                        break;
                    case FlowConstant.GROUP_TYPE_DEPT_POST_VAR:
                        candidateIdentity.setCandidateId(groupData.getDeptPostId());
                        insert(candidateIdentity);
                        break;
                    case FlowConstant.GROUP_TYPE_SELF_DEPT_POST_VAR:
                        Object v = flowApiService.getProcessInstanceVariable(
                                instanceId, FlowConstant.SELF_DEPT_POST_PREFIX + groupData.getPostId());
                        if (v != null) {
                            candidateIdentity.setCandidateId(v.toString());
                            insert(candidateIdentity);
                        }
                        break;
                    case FlowConstant.GROUP_TYPE_UP_DEPT_POST_VAR:
                        Object v2 = flowApiService.getProcessInstanceVariable(
                                instanceId, FlowConstant.UP_DEPT_POST_PREFIX + groupData.getPostId());
                        if (v2 != null) {
                            candidateIdentity.setCandidateId(v2.toString());
                            insert(candidateIdentity);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteByProcessInstanceId(String processInstanceId) {
        this.baseMapper.deleteByProcessInstanceId(processInstanceId);
    }
}
