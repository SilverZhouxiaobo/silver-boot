package cn.silver.framework.web.controller.monitor;

import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.controller.BaseController;
import cn.silver.framework.monitor.domain.Server;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务器监控
 *
 * @author hb
 */
@RestController
@Api(tags = {"【服务器监控】"})
@RequestMapping("/monitor/server")
public class ServerController extends BaseController {
    @GetMapping()
    @ApiOperation("获取服务器监控信息")
    @PreAuthorize("@ss.hasPermi('monitor:server:list')")
    public Response<Server> getInfo() throws Exception {
        Server server = new Server();
        server.copyTo();
        return Response.success(server);
    }
}
