package cn.silver.framework.flow.controller;

import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.controller.BaseController;
import cn.silver.framework.core.page.PageBean;
import cn.silver.framework.core.page.PageBuilder;
import cn.silver.framework.core.page.ResponsePageInfo;
import cn.silver.framework.flow.domain.FlowInstance;
import cn.silver.framework.flow.service.IFlowInstanceService;
import cn.silver.framework.workflow.service.IFlowApiService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>工作流流程实例管理<p>
 *
 * @author XuanXuan
 * @date 2021-04-03
 */
@Slf4j
@RestController
@RequestMapping("/flowable/instance")
@Api(tags = {"流程管理-流程实例管理"})
public class FlowInstanceController extends BaseController {

    @Autowired
    private IFlowInstanceService flowInstanceService;
    @Autowired
    private IFlowApiService apiService;

    @GetMapping("/list")
    @ApiOperation(value = "流程实例管理-分页列表查询")
    public ResponsePageInfo<FlowInstance> list(@ModelAttribute FlowInstance entity) throws ParseException {
        PageBean pageParam = PageBuilder.buildPageRequest();
        PageInfo<FlowInstance> pageData = flowInstanceService.selectPage(pageParam, entity);
        return toResponsePageInfo(pageData.getList(), pageData.getTotal());
    }

    /**
     * 根据输入参数查询，当前用户的历史流程数据。
     *
     * @param processDefinitionName 流程名。
     * @param beginDate             流程发起开始时间。
     * @param endDate               流程发起结束时间。
     * @return 查询结果应答。
     */
    @SneakyThrows
    @GetMapping("/history")
    public ResponsePageInfo<Map<String, Object>> listHistoricProcessInstance(@RequestParam(required = false) String processDefinitionName,
                                                                             @RequestParam(required = false) String beginDate,
                                                                             @RequestParam(required = false) String endDate) {
        String loginName = this.getUserId();
        PageBean pageParam = PageBuilder.buildPageRequest();
        PageInfo<HistoricProcessInstance> pageData = apiService.getHistoricProcessInstanceList(
                null, processDefinitionName, loginName, beginDate, endDate, pageParam, true);
        List<Map<String, Object>> resultList = new LinkedList<>();
        pageData.getList().forEach(instance -> resultList.add(BeanUtil.beanToMap(instance)));
        return toResponsePageInfo(resultList, pageData.getTotal());
    }

    @PostMapping("/listAllHistoricProcessInstance")
    public ResponsePageInfo<Map<String, Object>> listAllHistoricProcessInstance(@RequestParam(required = false) String processDefinitionName,
                                                                                @RequestParam(required = false) String startUser,
                                                                                @RequestParam(required = false) String beginDate,
                                                                                @RequestParam(required = false) String endDate) {
        PageBean pageParam = PageBuilder.buildPageRequest();
        PageInfo<HistoricProcessInstance> pageData = apiService.getHistoricProcessInstanceList(
                null, processDefinitionName, startUser, beginDate, endDate, pageParam, false);
        List<Map<String, Object>> resultList = new LinkedList<>();
        pageData.getList().forEach(instance -> resultList.add(BeanUtil.beanToMap(instance)));
        return toResponsePageInfo(resultList, pageData.getTotal());
    }

    /**
     * 获取流程图高亮数据。
     *
     * @param instanceId 流程实例Id。
     * @return 流程图高亮数据。
     */
    @GetMapping("/view/{instanceId}")
    public Response<JSONObject> view(@PathVariable String instanceId) {
        HistoricProcessInstance hpi = apiService.getHistoricProcessInstance(instanceId);
        BpmnModel bpmnModel = apiService.getBpmnModelByDefinitionId(hpi.getProcessDefinitionId());
        List<Process> processList = bpmnModel.getProcesses();
        List<FlowElement> flowElementList = new LinkedList<>();
        processList.forEach(p -> flowElementList.addAll(p.getFlowElements()));
        Map<String, String> allSequenceFlowMap = new HashMap<>(16);
        for (FlowElement flowElement : flowElementList) {
            if (flowElement instanceof SequenceFlow) {
                SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
                String ref = sequenceFlow.getSourceRef();
                String targetRef = sequenceFlow.getTargetRef();
                allSequenceFlowMap.put(ref + targetRef, sequenceFlow.getId());
            }
        }
        Set<String> finishedTaskSet = new LinkedHashSet<>();
        //获取流程实例的历史节点(全部执行过的节点，被拒绝的任务节点将会出现多次)
        List<HistoricActivityInstance> activityInstanceList =
                apiService.getHistoricActivityInstanceList(instanceId);
        List<String> activityInstanceTask = activityInstanceList.stream()
                .filter(s -> !StrUtil.equals(s.getActivityType(), "sequenceFlow"))
                .map(HistoricActivityInstance::getActivityId).collect(Collectors.toList());
        Set<String> finishedTaskSequenceSet = new LinkedHashSet<>();
        for (int i = 0; i < activityInstanceTask.size(); i++) {
            String current = activityInstanceTask.get(i);
            if (i != activityInstanceTask.size() - 1) {
                String next = activityInstanceTask.get(i + 1);
                finishedTaskSequenceSet.add(current + next);
            }
            finishedTaskSet.add(current);
        }
        Set<String> finishedSequenceFlowSet = new HashSet<>();
        finishedTaskSequenceSet.forEach(s -> finishedSequenceFlowSet.add(allSequenceFlowMap.get(s)));
        //获取流程实例当前正在待办的节点
        List<HistoricActivityInstance> unfinishedInstanceList =
                apiService.getHistoricUnfinishedInstanceList(instanceId);
        Set<String> unfinishedTaskSet = new LinkedHashSet<>();
        for (HistoricActivityInstance unfinishedActivity : unfinishedInstanceList) {
            unfinishedTaskSet.add(unfinishedActivity.getActivityId());
        }
        JSONObject jsonData = new JSONObject();
        jsonData.put("finishedTaskSet", finishedTaskSet);
        jsonData.put("finishedSequenceFlowSet", finishedSequenceFlowSet);
        jsonData.put("unfinishedTaskSet", unfinishedTaskSet);
        return Response.success(jsonData);
    }

    @PostMapping(value = "/active/{instanceId}")
    @ApiOperation(value = "流程实例管理-激活流程实例")
    public Response<Object> active(@PathVariable String instanceId) {
        flowInstanceService.active(instanceId);
        return Response.success();
    }

    @PostMapping(value = "/suspend/{instanceId}")
    @ApiOperation(value = "流程实例管理-挂起流程实例")
    public Response<Object> suspend(@PathVariable String instanceId) {
        flowInstanceService.suspend(instanceId);
        return Response.success();
    }

    @PostMapping(value = "/stop/{instanceId}")
    @ApiOperation("流程实例管理-结束流程实例")
    public Response<Void> stop(@PathVariable("instanceId") String instanceId, @RequestBody String reson) {
        flowInstanceService.stop(instanceId, reson, false);
        return Response.success();
    }

    @ApiOperation(value = "删除流程实例")
    @DeleteMapping(value = "/{instanceId}")
    public Response<Object> delete(@PathVariable String instanceId, @ApiParam(value = "删除原因") @RequestParam(required = false) String deleteReason) {
        flowInstanceService.delete(instanceId);
        return Response.success();
    }
}