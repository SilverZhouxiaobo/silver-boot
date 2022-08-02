package cn.silver.framework.workflow.controller;

import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.core.page.ResponsePageInfo;
import cn.silver.framework.workflow.constant.FlowMessageType;
import cn.silver.framework.workflow.domain.FlowMessage;
import cn.silver.framework.workflow.service.IFlowApiService;
import cn.silver.framework.workflow.service.IFlowMessageCandidateService;
import cn.silver.framework.workflow.service.IFlowMessageOperationService;
import cn.silver.framework.workflow.service.IFlowMessageService;
import cn.silver.framework.workflow.util.FlowCustomExtFactory;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工作流消息操作控制器类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@RestController
@Api(tags = "工作流消息操作接口")
@RequestMapping("/flow/message")
public class FlowMessageController extends DataController<IFlowMessageService, FlowMessage> {

    @Autowired
    private IFlowApiService apiService;
    @Autowired
    private FlowCustomExtFactory extFactory;
    @Autowired
    private IFlowMessageOperationService operationService;
    @Autowired
    private IFlowMessageCandidateService candidateService;

    /**
     * 获取当前用户的未读消息总数。
     * NOTE：白名单接口。
     *
     * @return 应答结果对象，包含当前用户的未读消息总数。
     */
    @GetMapping("/count")
    public Response<JSONObject> getMessageCount() {
        JSONObject resultData = new JSONObject();
        resultData.put("remindingMessageCount", baseService.countRemindingMessageListByUser());
        resultData.put("copyMessageCount", baseService.countCopyMessageByUser());
        return Response.success(resultData);
    }

    /**
     * 根据消息Id，获取流程Id关联的业务数据。
     * NOTE：白名单接口。
     *
     * @param id       抄送消息Id。
     * @param snapshot 是否获取抄送或传阅时任务的业务快照数据。如果为true，后续任务导致的业务数据修改，将不会返回给前端。
     * @return 抄送消息关联的流程实例业务数据。
     */
    @GetMapping("/viewCopyBusinessData/{id}")
    public Response<JSONObject> viewCopyBusinessData(@PathVariable String id, @RequestParam(required = false) Boolean snapshot) {
        String errorMessage;
        // 验证流程任务的合法性。
        FlowMessage flowMessage = baseService.selectById(id);
        if (flowMessage == null) {
            return Response.error(ResponseEnum.DATA_ERROR_NOT_FOUND);
        }
        if (flowMessage.getMessageType() != FlowMessageType.COPY_TYPE.getCode()) {
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), "数据验证失败，当前消息不是抄送类型消息！");
        }
        if (flowMessage.getOnlineFormData() == null || flowMessage.getOnlineFormData()) {
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), "数据验证失败，当前消息为在线表单数据，不能通过该接口获取！");
        }
        if (!candidateService.isCandidateIdentityOnMessage(id)) {
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), "数据验证失败，当前用户没有权限访问该消息！");
        }
        JSONObject businessObject = null;
        if (snapshot != null && snapshot) {
            if (StrUtil.isNotBlank(flowMessage.getBusinessDataShot())) {
                businessObject = JSON.parseObject(flowMessage.getBusinessDataShot());
            }
            return Response.success(businessObject);
        }
        ProcessInstance instance = apiService.getProcessInstance(flowMessage.getProcessInstanceId());
        // 如果业务主数据为空，则直接返回。
        if (StrUtil.isBlank(instance.getBusinessKey())) {
            errorMessage = "数据验证失败，当前消息为所属流程实例没有包含业务主键Id！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
//        String businessData = extFactory.getBusinessDataExtHelper().getBusinessData(
//                flowMessage.getProcessDefinitionKey(), flowMessage.getProcessInstanceId(), instance.getBusinessKey());
//        if (StrUtil.isNotBlank(businessData)) {
//            businessObject = JSON.parseObject(businessData);
//        }
        // 将当前消息更新为已读
        operationService.readCopyTask(id);
        return Response.success(businessObject);
    }

    /**
     * 获取当前用户的催办消息列表。
     * 不仅仅包含，其中包括当前用户所属角色、部门和岗位的候选组催办消息。
     * NOTE：白名单接口。
     *
     * @return 应答结果对象，包含查询结果集。
     */
    @GetMapping("/listRemindingTask")
    public ResponsePageInfo<FlowMessage> listRemindingTask() {
        List<FlowMessage> flowMessageList = this.baseService.getRemindingMessageListByUser();
        return toResponsePageInfo(flowMessageList);
    }

    /**
     * 获取当前用户的抄送消息列表。
     * 不仅仅包含，其中包括当前用户所属角色、部门和岗位的候选组抄送消息。
     * NOTE：白名单接口。
     *
     * @param read true表示已读，false表示未读。
     * @return 应答结果对象，包含查询结果集。
     */
    @GetMapping("/listCopyMessage")
    public ResponsePageInfo<FlowMessage> listCopyMessage(@RequestParam Boolean read) {
        List<FlowMessage> flowMessageList = this.baseService.getCopyMessageListByUser(read);
        return toResponsePageInfo(flowMessageList);
    }

    /**
     * 读取抄送消息，同时更新当前用户对指定抄送消息的读取状态。
     *
     * @param messageId 消息Id。
     * @return 应答结果对象。
     */
    @PostMapping("/readCopyTask")
    public Response<Void> readCopyTask(@RequestBody String messageId) {
        String errorMessage;
        // 验证流程任务的合法性。
        FlowMessage flowMessage = this.baseService.selectById(messageId);
        if (flowMessage == null) {
            return Response.error(ResponseEnum.DATA_ERROR_NOT_FOUND);
        }
        if (!FlowMessageType.COPY_TYPE.equals(flowMessage.getMessageType())) {
            errorMessage = "数据验证失败，当前消息不是抄送类型消息！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
        if (!this.candidateService.isCandidateIdentityOnMessage(messageId)) {
            errorMessage = "数据验证失败，当前用户没有权限访问该消息！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
        this.operationService.readCopyTask(messageId);
        return Response.success();
    }
}
