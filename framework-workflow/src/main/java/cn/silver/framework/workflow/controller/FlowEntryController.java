package cn.silver.framework.workflow.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.workflow.constant.FlowConstant;
import cn.silver.framework.workflow.constant.FlowTaskType;
import cn.silver.framework.workflow.domain.FlowEntry;
import cn.silver.framework.workflow.domain.FlowEntryVariable;
import cn.silver.framework.workflow.domain.FlowTaskExt;
import cn.silver.framework.workflow.object.FlowTaskMultiSignAssign;
import cn.silver.framework.workflow.service.IFlowApiService;
import cn.silver.framework.workflow.service.IFlowEntryService;
import cn.silver.framework.workflow.service.IFlowEntryVariableService;
import cn.silver.framework.workflow.vo.TaskInfoVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.api.FlowableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.stream.XMLStreamException;
import java.util.*;

/**
 * 工作流操作控制器类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@RestController
@Api(tags = "工作流操作接口")
@RequestMapping("/flow/entry")
public class FlowEntryController extends DataController<IFlowEntryService, FlowEntry> {
    @Autowired
    private IFlowEntryVariableService flowEntryVariableService;
    @Autowired
    private IFlowApiService flowApiService;

    public FlowEntryController() {
        this.authorize = "flow:entry";
        this.title = "工作流定义管理";
    }

    /**
     * 发布工作流。
     *
     * @param id 流程主键Id。
     * @return 应答结果对象。
     */
    @PostMapping("/deploy/{id}")
    public Response<Void> publish(@PathVariable("id") String id) throws XMLStreamException {
        String errorMessage;
        FlowEntry flowEntry = this.baseService.selectById(id);
        if (flowEntry == null) {
            errorMessage = "数据验证失败，该流程并不存在，请刷新后重试！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
        if (StrUtil.isBlank(flowEntry.getBpmnXml())) {
            errorMessage = "数据验证失败，该流程没有流程图不能被发布！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
        Response<TaskInfoVo> taskInfoResult = this.verifyAndGetInitialTaskInfo(flowEntry);
        if (!taskInfoResult.isSuccess()) {
            return Response.error(taskInfoResult.getCode(), taskInfoResult.getMsg());
        }
        List<FlowTaskExt> flowTaskExtList = this.buildTaskExtList(flowEntry);
        String taskInfo = taskInfoResult.getData() == null ? null : JSON.toJSONString(taskInfoResult.getData());
        this.baseService.publish(flowEntry, taskInfo, flowTaskExtList);
        return Response.success();
    }

    //    /**
//     * 以字典形式返回全部FlowEntry数据集合。字典的键值为[entryId, procDefinitionName]。
//     * 白名单接口，登录用户均可访问。
//     *
//     * @param filter 过滤对象。
//     * @return 应答结果对象，包含的数据为 List<Map<String, String>>，map中包含两条记录，key的值分别是id和name，value对应具体数据。
//     */
//    @Override
//    @GetMapping("/listDict")
//    public Response<List<DictModel>> listDict(FlowEntry filter) {
//        List<FlowEntry> resultList = this.baseService.selectList(filter);
//        List<DictModel> dictModels = resultList.stream().map(entry -> new DictModel(entry.getProcessDefinitionName(), entry.getId()))
//                .collect(Collectors.toList());
//        return Response.success(dictModels);
//    }
//
//    /**
//     * 白名单接口，根据流程Id，获取流程引擎需要的流程标识和流程名称。
//     *
//     * @param entryId 流程Id。
//     * @return 流程的部分数据。
//     */
//    @GetMapping("/viewDict")
//    public Response<Map<String, Object>> viewDict(@RequestParam String entryId) {
//        FlowEntry flowEntry = this.baseService.selectById(entryId);
//        if (flowEntry == null) {
//            return Response.error(ResponseEnum.DATA_ERROR_NOT_FOUND);
//        }
//        Map<String, Object> resultMap = new HashMap<>(2);
//        resultMap.put("processDefinitionKey", flowEntry.getProcessDefinitionKey());
//        resultMap.put("processDefinitionName", flowEntry.getProcessDefinitionName());
//        return Response.success(resultMap);
//    }
    private FlowTaskExt buildTaskExt(UserTask userTask) {
        FlowTaskExt flowTaskExt = new FlowTaskExt();
        flowTaskExt.setTaskId(userTask.getId());
        String formKey = userTask.getFormKey();
        if (StrUtil.isNotBlank(formKey)) {
            TaskInfoVo taskInfoVo = JSON.parseObject(formKey, TaskInfoVo.class);
            flowTaskExt.setGroupType(taskInfoVo.getGroupType());
        }
        Map<String, List<ExtensionElement>> extensionMap = userTask.getExtensionElements();
        if (MapUtil.isNotEmpty(extensionMap)) {
            List<JSONObject> operationList = this.buildOperationListExtensionElement(extensionMap);
            if (CollUtil.isNotEmpty(operationList)) {
                flowTaskExt.setOperationListJson(JSON.toJSONString(operationList));
            }
            List<JSONObject> variableList = this.buildVariableListExtensionElement(extensionMap);
            if (CollUtil.isNotEmpty(variableList)) {
                flowTaskExt.setVariableListJson(JSON.toJSONString(variableList));
            }
            JSONObject assigneeListObject = this.buildAssigneeListExtensionElement(extensionMap);
            if (assigneeListObject != null) {
                flowTaskExt.setAssigneeListJson(JSON.toJSONString(assigneeListObject));
            }
            List<JSONObject> deptPostList = this.buildDeptPostListExtensionElement(extensionMap);
            if (deptPostList != null) {
                flowTaskExt.setDeptPostListJson(JSON.toJSONString(deptPostList));
            }
            List<JSONObject> copyList = this.buildCopyListExtensionElement(extensionMap);
            if (copyList != null) {
                flowTaskExt.setCopyListJson(JSON.toJSONString(copyList));
            }
            JSONObject candidateGroupObject = this.buildUserCandidateGroupsExtensionElement(extensionMap);
            if (candidateGroupObject != null) {
                String type = candidateGroupObject.getString("type");
                String value = candidateGroupObject.getString("value");
                switch (type) {
                    case "DEPT":
                        flowTaskExt.setDeptIds(value);
                        break;
                    case "ROLE":
                        flowTaskExt.setRoleIds(value);
                        break;
                    case "USERS":
                        flowTaskExt.setCandidateUsernames(value);
                        break;
                    default:
                        break;
                }
            }
        }
        return flowTaskExt;
    }

    private List<FlowTaskExt> buildTaskExtList(FlowEntry flowEntry) throws XMLStreamException {
        List<FlowTaskExt> flowTaskExtList = new LinkedList<>();
        BpmnModel bpmnModel = flowApiService.convertToBpmnModel(flowEntry.getBpmnXml());
        List<Process> processList = bpmnModel.getProcesses();
        for (Process process : processList) {
            for (FlowElement element : process.getFlowElements()) {
                if (element instanceof UserTask) {
                    FlowTaskExt flowTaskExt = this.buildTaskExt((UserTask) element);
                    flowTaskExtList.add(flowTaskExt);
                }
            }
        }
        return flowTaskExtList;
    }

    private Response<TaskInfoVo> verifyAndGetInitialTaskInfo(FlowEntry flowEntry) throws XMLStreamException {
        String errorMessage;
        BpmnModel bpmnModel = flowApiService.convertToBpmnModel(flowEntry.getBpmnXml());
        Process process = bpmnModel.getMainProcess();
        if (process == null) {
            errorMessage = "数据验证失败，当前流程标识 [" + flowEntry.getProcessDefinitionKey() + "] 关联的流程模型并不存在！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
        Collection<FlowElement> elementList = process.getFlowElements();
        FlowElement startEvent = null;
        FlowElement firstTask = null;
        // 这里我们只定位流程模型中的第二个节点。
        for (FlowElement flowElement : elementList) {
            if (flowElement instanceof StartEvent) {
                startEvent = flowElement;
                break;
            }
        }
        if (startEvent == null) {
            errorMessage = "数据验证失败，当前流程图没有包含 [开始事件] 节点，请修改流程图！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
        for (FlowElement flowElement : elementList) {
            if (flowElement instanceof SequenceFlow) {
                SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
                if (sequenceFlow.getSourceFlowElement().equals(startEvent)) {
                    firstTask = sequenceFlow.getTargetFlowElement();
                    break;
                }
            }
        }
        if (firstTask == null) {
            errorMessage = "数据验证失败，当前流程图没有包含 [开始事件] 节点没有任何连线，请修改流程图！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
        TaskInfoVo taskInfoVo;
        if (firstTask instanceof UserTask) {
            UserTask userTask = (UserTask) firstTask;
            String formKey = userTask.getFormKey();
            if (StrUtil.isNotBlank(formKey)) {
                taskInfoVo = JSON.parseObject(formKey, TaskInfoVo.class);
            } else {
                taskInfoVo = new TaskInfoVo();
            }
            taskInfoVo.setAssignee(userTask.getAssignee());
            taskInfoVo.setTaskKey(userTask.getId());
            taskInfoVo.setTaskType(FlowTaskType.USER_TYPE.getCode());
            Map<String, List<ExtensionElement>> extensionMap = userTask.getExtensionElements();
            if (MapUtil.isNotEmpty(extensionMap)) {
                taskInfoVo.setOperationList(this.buildOperationListExtensionElement(extensionMap));
                taskInfoVo.setVariableList(this.buildVariableListExtensionElement(extensionMap));
            }
        } else {
            taskInfoVo = new TaskInfoVo();
            taskInfoVo.setTaskType(FlowTaskType.OTHER_TYPE.getCode());
        }
        return Response.success(taskInfoVo);
    }

    private JSONObject buildUserCandidateGroupsExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        JSONObject jsonData = null;
        List<ExtensionElement> elementCandidateGroupsList = extensionMap.get("userCandidateGroups");
        if (CollUtil.isEmpty(elementCandidateGroupsList)) {
            return jsonData;
        }
        jsonData = new JSONObject();
        ExtensionElement ee = elementCandidateGroupsList.get(0);
        jsonData.put("type", ee.getAttributeValue(null, "type"));
        jsonData.put("value", ee.getAttributeValue(null, "value"));
        return jsonData;
    }

    private JSONObject buildAssigneeListExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        JSONObject jsonData = null;
        List<ExtensionElement> elementAssigneeList = extensionMap.get("assigneeList");
        if (CollUtil.isEmpty(elementAssigneeList)) {
            return jsonData;
        }
        ExtensionElement ee = elementAssigneeList.get(0);
        Map<String, List<ExtensionElement>> childExtensionMap = ee.getChildElements();
        if (MapUtil.isEmpty(childExtensionMap)) {
            return jsonData;
        }
        List<ExtensionElement> assigneeElements = childExtensionMap.get("assignee");
        if (CollUtil.isEmpty(assigneeElements)) {
            return jsonData;
        }
        JSONArray assigneeIdArray = new JSONArray();
        for (ExtensionElement e : assigneeElements) {
            assigneeIdArray.add(e.getAttributeValue(null, "id"));
        }
        jsonData = new JSONObject();
        String assigneeType = ee.getAttributeValue(null, "type");
        jsonData.put("assigneeType", assigneeType);
        jsonData.put("assigneeList", assigneeIdArray);
        return jsonData;
    }

    private List<JSONObject> buildOperationListExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        List<ExtensionElement> formOperationElements =
                this.getMyExtensionElementList(extensionMap, "operationList", "formOperation");
        if (CollUtil.isEmpty(formOperationElements)) {
            return null;
        }
        List<JSONObject> resultList = new LinkedList<>();
        for (ExtensionElement e : formOperationElements) {
            JSONObject operationJsonData = new JSONObject();
            operationJsonData.put("id", e.getAttributeValue(null, "id"));
            operationJsonData.put("label", e.getAttributeValue(null, "label"));
            operationJsonData.put("type", e.getAttributeValue(null, "type"));
            operationJsonData.put("showOrder", e.getAttributeValue(null, "showOrder"));
            String multiSignAssignee = e.getAttributeValue(null, "multiSignAssignee");
            if (StrUtil.isNotBlank(multiSignAssignee)) {
                operationJsonData.put("multiSignAssignee",
                        JSON.parseObject(multiSignAssignee, FlowTaskMultiSignAssign.class));
            }
            resultList.add(operationJsonData);
        }
        return resultList;
    }

    private List<JSONObject> buildVariableListExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        List<ExtensionElement> formVariableElements =
                this.getMyExtensionElementList(extensionMap, "variableList", "formVariable");
        if (CollUtil.isEmpty(formVariableElements)) {
            return null;
        }
        Set<String> variableIdSet = new HashSet<>();
        for (ExtensionElement e : formVariableElements) {
            String id = e.getAttributeValue(null, "id");
            variableIdSet.add(id);
        }
        List<FlowEntryVariable> variableList = flowEntryVariableService.selectByIds(variableIdSet);
        List<JSONObject> resultList = new LinkedList<>();
        for (FlowEntryVariable variable : variableList) {
            resultList.add((JSONObject) JSON.toJSON(variable));
        }
        return resultList;
    }

    private List<JSONObject> buildDeptPostListExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        List<ExtensionElement> deptPostElements =
                this.getMyExtensionElementList(extensionMap, "deptPostList", "deptPost");
        if (CollUtil.isEmpty(deptPostElements)) {
            return null;
        }
        List<JSONObject> resultList = new LinkedList<>();
        for (ExtensionElement e : deptPostElements) {
            JSONObject deptPostJsonData = new JSONObject();
            deptPostJsonData.put("id", e.getAttributeValue(null, "id"));
            deptPostJsonData.put("type", e.getAttributeValue(null, "type"));
            String postId = e.getAttributeValue(null, "postId");
            if (postId != null) {
                deptPostJsonData.put("postId", postId);
            }
            String deptPostId = e.getAttributeValue(null, "deptPostId");
            if (deptPostId != null) {
                deptPostJsonData.put("deptPostId", deptPostId);
            }
            resultList.add(deptPostJsonData);
        }
        return resultList;
    }

    private List<JSONObject> buildCopyListExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        List<ExtensionElement> copyElements =
                this.getMyExtensionElementList(extensionMap, "copyItemList", "copyItem");
        if (CollUtil.isEmpty(copyElements)) {
            return null;
        }
        List<JSONObject> resultList = new LinkedList<>();
        for (ExtensionElement e : copyElements) {
            JSONObject copyJsonData = new JSONObject();
            String type = e.getAttributeValue(null, "type");
            copyJsonData.put("type", type);
            if (!StrUtil.equalsAny(type, FlowConstant.GROUP_TYPE_DEPT_POST_LEADER_VAR,
                    FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER_VAR,
                    FlowConstant.GROUP_TYPE_USER_VAR,
                    FlowConstant.GROUP_TYPE_ROLE_VAR,
                    FlowConstant.GROUP_TYPE_DEPT_VAR,
                    FlowConstant.GROUP_TYPE_DEPT_POST_VAR,
                    FlowConstant.GROUP_TYPE_ALL_DEPT_POST_VAR,
                    FlowConstant.GROUP_TYPE_SELF_DEPT_POST_VAR,
                    FlowConstant.GROUP_TYPE_UP_DEPT_POST_VAR)) {
                throw new FlowableException("Invalid TYPE [" + type + " ] for CopyItenList Extension!");
            }
            String id = e.getAttributeValue(null, "id");
            if (StrUtil.isNotBlank(id)) {
                copyJsonData.put("id", id);
            }
            resultList.add(copyJsonData);
        }
        return resultList;
    }

    private List<ExtensionElement> getMyExtensionElementList(
            Map<String, List<ExtensionElement>> extensionMap, String rootName, String childName) {
        List<ExtensionElement> elementList = extensionMap.get(rootName);
        if (CollUtil.isEmpty(elementList)) {
            return Collections.emptyList();
        }
        ExtensionElement ee = elementList.get(0);
        Map<String, List<ExtensionElement>> childExtensionMap = ee.getChildElements();
        if (MapUtil.isEmpty(childExtensionMap)) {
            return Collections.emptyList();
        }
        List<ExtensionElement> childrenElements = childExtensionMap.get(childName);
        if (CollUtil.isEmpty(childrenElements)) {
            return Collections.emptyList();
        }
        return childrenElements;
    }
}
