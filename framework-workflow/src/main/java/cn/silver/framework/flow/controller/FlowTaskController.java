package cn.silver.framework.flow.controller;

import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.controller.BaseController;
import cn.silver.framework.core.page.PageBean;
import cn.silver.framework.core.page.PageBuilder;
import cn.silver.framework.core.page.ResponsePageInfo;
import cn.silver.framework.flow.service.IFlowTaskService;
import cn.silver.framework.workflow.service.IFlowApiService;
import cn.silver.framework.workflow.vo.FlowTaskVo;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>工作流任务管理<p>
 *
 * @author XuanXuan
 * @date 2021-04-03
 */
@Slf4j
@RestController
@Api(tags = "流程管理-任务管理")
@RequestMapping("/flowable/task")
public class FlowTaskController extends BaseController {

    @Autowired
    private IFlowTaskService taskService;
    @Autowired
    private IFlowApiService apiService;

    /**
     * 返回当前用户待办的任务列表。
     *
     * @return 返回当前用户待办的任务列表。如果指定流程标识，则仅返回该流程的待办任务列表。
     */
    @GetMapping("/runtime")
    public ResponsePageInfo<FlowTaskVo> listRuntimeTask(@ModelAttribute FlowTaskVo vo) {
        PageBean page = PageBuilder.buildPageRequest();
        PageInfo<FlowTaskVo> pageData = taskService.todoList(page, vo);
        return toResponsePageInfo(pageData.getList(), pageData.getTotal());
    }

    /**
     * 返回当前用户待办的任务数量。
     *
     * @return 返回当前用户待办的任务数量。
     */
    @PostMapping("/count")
    public Response<Long> countRuntimeTask() {
        String username = this.getLoginUser().getId();
        long totalCount = apiService.getTaskCountByUserName(username);
        return Response.success(totalCount);
    }

    /**
     * 获取当前用户的已办理的审批任务列表。
     *
     * @return 查询结果应答。
     */
    @SneakyThrows
    @GetMapping("/history")
    public ResponsePageInfo<FlowTaskVo> listHistoricTask(@ModelAttribute FlowTaskVo vo) {
        PageBean pageParam = PageBuilder.buildPageRequest();
        PageInfo<FlowTaskVo> pageData = this.taskService.doneList(pageParam, vo);
        return toResponsePageInfo(pageData.getList(), pageData.getTotal());
    }

    /**
     * 生成流程图
     *
     * @param processId 任务ID
     */
    @RequestMapping("/diagram/{processId}")
    public void genProcessDiagram(HttpServletResponse response,
                                  @PathVariable("processId") String processId) {
        InputStream inputStream = taskService.diagram(processId);
        OutputStream os = null;
        BufferedImage image = null;
        try {
            image = ImageIO.read(inputStream);
            response.setContentType("image/png");
            os = response.getOutputStream();
            if (image != null) {
                ImageIO.write(image, "png", os);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
