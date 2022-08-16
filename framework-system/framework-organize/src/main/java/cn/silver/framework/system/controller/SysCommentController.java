package cn.silver.framework.system.controller;


import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.system.domain.SysComment;
import cn.silver.framework.system.service.ISysCommentService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统评论记录Controller
 *
 * @author hb
 * @date 2022-07-06
 */

@RestController
@Api(tags = {"系统评论记录"})
@RequestMapping("/sys/comment")
public class SysCommentController extends DataController<ISysCommentService, SysComment> {

    public SysCommentController() {
        this.authorize = "sys:comment";
        this.title = "系统评论记录";
    }
}
