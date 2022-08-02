package cn.silver.framework.web.controller.monitor;

import cn.silver.framework.common.constant.Constants;
import cn.silver.framework.common.utils.StringUtils;
import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.controller.BaseController;
import cn.silver.framework.core.page.ResponsePageInfo;
import cn.silver.framework.core.redis.RedisCache;
import cn.silver.framework.monitor.annotation.Log;
import cn.silver.framework.monitor.constant.BusinessType;
import cn.silver.framework.system.domain.SysUserOnline;
import cn.silver.framework.system.service.ISysUserOnlineService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 在线用户监控
 *
 * @author hb
 */
@RestController
@RequestMapping("/monitor/online")
@Api(tags = {"【在线用户监控】"})
public class SysUserOnlineController extends BaseController {
    @Autowired
    private ISysUserOnlineService userOnlineService;

    @Autowired
    private RedisCache redisCache;

    @PreAuthorize("@ss.hasPermi('monitor:online:list')")
    @GetMapping("/list")
    @ApiOperation("查询在线用户监控列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页码", dataType = "int", paramType = "query", required = false),
            @ApiImplicitParam(name = "pageSize", value = "每页数据量", dataType = "int", paramType = "query", required = false),
    })
    public ResponsePageInfo<SysUserOnline> list(
            @ApiParam(name = "ipaddr", value = "ip地址")
            @RequestParam(name = "ipaddr", required = false) String ipaddr,

            @ApiParam(name = "userName", value = "用户名")
            @RequestParam(name = "userName", required = false) String userName
    ) {
        Collection<String> keys = redisCache.keys(Constants.LOGIN_TOKEN_KEY + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<SysUserOnline>();
        for (String key : keys) {
            LoginUser user = redisCache.getCacheObject(key);
            if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName)) {
                if (StringUtils.equals(ipaddr, user.getIpaddr()) && StringUtils.equals(userName, user.getUsername())) {
                    userOnlineList.add(userOnlineService.selectOnlineByInfo(ipaddr, userName, user));
                }
            } else if (StringUtils.isNotEmpty(ipaddr)) {
                if (StringUtils.equals(ipaddr, user.getIpaddr())) {
                    userOnlineList.add(userOnlineService.selectOnlineByIpaddr(ipaddr, user));
                }
            } else if (StringUtils.isNotEmpty(userName) && StringUtils.isNotNull(user)) {
                if (StringUtils.equals(userName, user.getUsername())) {
                    userOnlineList.add(userOnlineService.selectOnlineByUserName(userName, user));
                }
            } else {
                userOnlineList.add(userOnlineService.loginUserToUserOnline(user));
            }
        }
        Collections.reverse(userOnlineList);
        userOnlineList.removeAll(Collections.singleton(null));
        return toResponsePageInfo(userOnlineList);
    }

    /**
     * 强退用户
     */
    @DeleteMapping("/{tokenId}")
    @ApiOperation("强退用户")
    @PreAuthorize("@ss.hasPermi('monitor:online:forceLogout')")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    public Response forceLogout(
            @ApiParam(name = "tokenId", value = "令牌tokenId", required = true)
            @PathVariable("tokenId") String tokenId
    ) {
        redisCache.deleteObject(Constants.LOGIN_TOKEN_KEY + tokenId);
        return Response.success();
    }
}
