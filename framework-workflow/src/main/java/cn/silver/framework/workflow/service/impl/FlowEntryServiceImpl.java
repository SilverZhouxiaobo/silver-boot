package cn.silver.framework.workflow.service.impl;

import cn.silver.framework.common.exception.CustomException;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.workflow.constant.FlowConstant;
import cn.silver.framework.workflow.constant.FlowPublishStatus;
import cn.silver.framework.workflow.domain.FlowCategory;
import cn.silver.framework.workflow.domain.FlowEntry;
import cn.silver.framework.workflow.domain.FlowEntryPublish;
import cn.silver.framework.workflow.domain.FlowTaskExt;
import cn.silver.framework.workflow.listener.FlowFinishedListener;
import cn.silver.framework.workflow.mapper.FlowEntryMapper;
import cn.silver.framework.workflow.object.FlowTaskPostCandidateGroup;
import cn.hb.software.gacim.workflow.service.*;
import cn.silver.framework.workflow.util.BaseFlowIdentityExtHelper;
import cn.silver.framework.workflow.util.FlowCustomExtFactory;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.silver.framework.workflow.service.*;
import com.alibaba.fastjson.JSON;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * FlowEntry数据操作服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("flowEntryService")
public class FlowEntryServiceImpl extends BaseServiceImpl<FlowEntryMapper, FlowEntry> implements IFlowEntryService {

    @Autowired
    private IFlowEntryPublishService publishService;
    @Autowired
    private IFlowEntryPublishVariableService publishVariableService;
    @Autowired
    private IFlowEntryVariableService flowEntryVariableService;
    @Autowired
    private IFlowCategoryService flowCategoryService;
    @Autowired
    private IFlowTaskExtService flowTaskExtService;
    @Autowired
    private IFlowApiService flowApiService;
    @Autowired
    private FlowCustomExtFactory flowCustomExtFactory;
    @Autowired
    private RepositoryService repositoryService;

    /**
     * 保存新增对象。
     *
     * @param entry 新增工作流对象。
     * @return 返回新增对象。
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insert(FlowEntry entry) {
        if (CollectionUtils.isNotEmpty(this.selectExists(entry))) {
            throw new CustomException("数据验证失败，该流程定义标识已存在！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
        }
        entry.preInsert();
        entry.setStatus(FlowPublishStatus.UNPUBLISHED.getCode());
        int result = baseMapper.insert(entry);
        this.flowEntryVariableService.init(entry.getId());
        return result;
    }

    /**
     * 更新数据对象。
     *
     * @param entity 更新的对象。
     * @return 成功返回true，否则false。
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int update(FlowEntry entity) {
        FlowEntry originalFlowEntry = this.selectById(entity.getId());
        if (originalFlowEntry == null) {
            throw new CustomException("数据验证失败，当前流程并不存在，请刷新后重试！", ResponseEnum.DATA_ERROR_NOT_FOUND.getCode());
        }
        if (CollectionUtils.isNotEmpty(this.selectExists(entity))) {
            throw new CustomException("数据验证失败，该流程定义标识已存在！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
        }
        if (originalFlowEntry.getStatus().equals(FlowPublishStatus.PUBLISHED.getCode())) {
            if (ObjectUtil.notEqual(entity.getProcessDefinitionKey(), originalFlowEntry.getProcessDefinitionKey())) {
                throw new CustomException("数据验证失败，当前流程为发布状态，流程标识不能修改！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
            }
            if (ObjectUtil.notEqual(entity.getCategoryId(), originalFlowEntry.getCategoryId())) {
                throw new CustomException("数据验证失败，当前流程为发布状态，流程分类不能修改！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
            }
        }
        entity.preUpdate();
        entity.setPageId(originalFlowEntry.getPageId());
        return baseMapper.update(entity);
    }

    /**
     * 删除指定数据。
     *
     * @param id 主键Id。
     * @return 成功返回true，否则false。
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int delete(String id) {
        // 验证关联Id的数据合法性
        FlowEntry originalFlowEntry = this.selectById(id);
        if (originalFlowEntry == null) {
            throw new CustomException("数据验证失败，当前流程并不存在，请刷新后重试！", ResponseEnum.DATA_ERROR_NOT_FOUND.getCode());
        }
        if (originalFlowEntry.getStatus().equals(FlowPublishStatus.PUBLISHED.getCode())) {
            throw new CustomException("数据验证失败，当前流程为发布状态，不能删除！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
        }
        flowEntryVariableService.removeByEntryId(id);
        return this.baseMapper.deleteByPrimaryKey(id);
    }

    /**
     * 发布指定流程。
     *
     * @param flowEntry       待发布的流程对象。
     * @param initTaskInfo    第一个非开始节点任务的附加信息。
     * @param flowTaskExtList 所有用户任务的自定义扩展数据列表。
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void publish(FlowEntry flowEntry, String initTaskInfo, List<FlowTaskExt> flowTaskExtList) throws XMLStreamException {
        FlowCategory category = flowCategoryService.selectByValue(flowEntry.getCategoryId());
        InputStream xmlStream = new ByteArrayInputStream(
                flowEntry.getBpmnXml().getBytes(StandardCharsets.UTF_8));
        @Cleanup XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(xmlStream);
        BpmnXMLConverter converter = new BpmnXMLConverter();
        BpmnModel bpmnModel = converter.convertToBpmnModel(reader);
        flowApiService.addProcessInstanceEndListener(bpmnModel, FlowFinishedListener.class);
        Collection<FlowElement> elementList = bpmnModel.getMainProcess().getFlowElements();
        Map<String, FlowElement> elementMap =
                elementList.stream().filter(e -> e instanceof UserTask).collect(Collectors.toMap(FlowElement::getId, Function.identity()));
        if (CollUtil.isNotEmpty(flowTaskExtList)) {
            BaseFlowIdentityExtHelper flowIdentityExtHelper = flowCustomExtFactory.getFlowIdentityExtHelper();
            for (FlowTaskExt t : flowTaskExtList) {
                UserTask userTask = (UserTask) elementMap.get(t.getTaskId());
                // 如果流程图中包含部门领导审批和上级部门领导审批的选项，就需要注册 FlowCustomExtFactory 工厂中的
                // BaseFlowIdentityExtHelper 对象，该注册操作需要业务模块中实现。
                if (StrUtil.equals(t.getGroupType(), FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER)) {
                    userTask.setCandidateGroups(
                            CollUtil.newArrayList("${" + FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER_VAR + "}"));
                    Assert.notNull(flowIdentityExtHelper);
                    flowApiService.addTaskCreateListener(userTask, flowIdentityExtHelper.getUpDeptPostLeaderListener());
                } else if (StrUtil.equals(t.getGroupType(), FlowConstant.GROUP_TYPE_DEPT_POST_LEADER)) {
                    userTask.setCandidateGroups(
                            CollUtil.newArrayList("${" + FlowConstant.GROUP_TYPE_DEPT_POST_LEADER_VAR + "}"));
                    Assert.notNull(flowIdentityExtHelper);
                    flowApiService.addTaskCreateListener(userTask, flowIdentityExtHelper.getDeptPostLeaderListener());
                } else if (StrUtil.equals(t.getGroupType(), FlowConstant.GROUP_TYPE_POST)) {
                    Assert.notNull(t.getDeptPostListJson());
                    List<FlowTaskPostCandidateGroup> groupDataList =
                            JSON.parseArray(t.getDeptPostListJson(), FlowTaskPostCandidateGroup.class);
                    List<String> candidateGroupList =
                            FlowTaskPostCandidateGroup.buildCandidateGroupList(groupDataList);
                    userTask.setCandidateGroups(candidateGroupList);
                }
            }
        }
        Deployment deploy = repositoryService.createDeployment()
                .addBpmnModel(flowEntry.getProcessDefinitionKey() + ".bpmn", bpmnModel)
                .name(flowEntry.getProcessDefinitionName())
                .key(flowEntry.getProcessDefinitionKey())
                .category(category.getCode())
                .deploy();
        ProcessDefinition processDefinition = flowApiService.getProcessDefinitionByDeployId(deploy.getId());
        if (CollUtil.isNotEmpty(flowTaskExtList)) {
            flowTaskExtList.forEach(t -> {
                t.setProcessDefinitionId(processDefinition.getId());
                flowTaskExtService.insertOrUpdate(t);
            });
        }
        FlowEntryPublish flowEntryPublish = new FlowEntryPublish(flowEntry.getId(), processDefinition, initTaskInfo, FlowPublishStatus.UNPUBLISHED.getCode().equals(flowEntry.getStatus()));
        publishService.insert(flowEntryPublish);
        // 对于从未发布过的工作，第一次发布的时候会将本地发布置位主版本。
//        if (FlowPublishStatus.UNPUBLISHED.getCode().equals(flowEntry.getStatus())) {
        flowEntry.setMainEntryPublishId(flowEntryPublish.getId());
        flowEntry.setPublishVersion(flowEntryPublish.getPublishVersion());
        flowEntry.setActiveStatus(flowEntryPublish.getActiveStatus());
//        }
        flowEntry.setStatus(FlowPublishStatus.PUBLISHED.getCode());
        flowEntry.setLatestPublishTime(new Date());
        baseMapper.update(flowEntry);
        this.publishVariableService.init(flowEntryPublish);
    }
}
