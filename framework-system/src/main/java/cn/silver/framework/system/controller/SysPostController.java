package cn.silver.framework.system.controller;

import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.system.domain.SysPost;
import cn.silver.framework.system.service.ISysPostService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 岗位信息操作处理
 *
 * @author hb
 */
@RestController
@Api(tags = {"岗位信息操作处理"})
@RequestMapping("/system/post")
public class SysPostController extends DataController<ISysPostService, SysPost> {
    public SysPostController() {
        this.authorize = "system:post";
        this.title = "岗位管理";
    }
}
