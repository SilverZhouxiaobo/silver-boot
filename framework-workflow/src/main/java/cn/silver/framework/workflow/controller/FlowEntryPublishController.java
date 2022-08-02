package cn.silver.framework.workflow.controller;

import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.workflow.domain.FlowEntryPublish;
import cn.silver.framework.workflow.service.IFlowEntryPublishService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Api(tags = "工作流发布接口")
@RequestMapping("/flow/entry/publish")
public class FlowEntryPublishController extends DataController<IFlowEntryPublishService, FlowEntryPublish> {

    public FlowEntryPublishController() {
        this.authorize = "flow:entry:publish";
        this.title = "工作流发布管理";
    }

    /**
     * 切换指定工作的发布主版本。
     *
     * @param params
     * @return 应答结果对象。
     */
    @PostMapping("/chagneMain")
    public Response<Void> updateMainVersion(@RequestBody JSONObject params) {
        this.baseService.changeMainVersion(params);
        return Response.success();
    }

    /**
     * 挂起工作流的指定发布版本。
     *
     * @param id 工作发布Id。
     * @return 应答结果对象。
     */
    @PostMapping("/suspend/{id}")
    public Response<Void> suspendFlowEntryPublish(@PathVariable String id) {
        this.baseService.suspend(id);
        return Response.success();
    }

    /**
     * 激活工作流的指定发布版本。
     *
     * @param id 工作发布Id。
     * @return 应答结果对象。
     */
    @PostMapping("/activate/{id}")
    public Response<Void> activateFlowEntryPublish(@PathVariable String id) {
        this.baseService.activate(id);
        return Response.success();
    }
}
