package cn.silver.framework.system.common.controller;

import cn.hutool.core.date.DateUtil;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.controller.BaseController;
import cn.silver.framework.system.domain.SysExternalLink;
import cn.silver.framework.system.service.ISysExternalLinkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author Administrator
 */
@Slf4j
@RestController
@Api(tags = "Api管理-系统API管理")
@RequestMapping("/api/system")
public class SystemApiController extends BaseController {

    @Autowired
    private ISysExternalLinkService linkService;

    @GetMapping(value = "/terminal/link/{id}")
    @ApiOperation(value = "外链信息查询", notes = "根据id查询外链信息")
    public Response<SysExternalLink> getInfo(@PathVariable("id") String id) {
        SysExternalLink entity = linkService.selectById(id);
        if (ObjectUtils.isEmpty(entity)) {
            return Response.error(ResponseEnum.NOT_FOUND);
        }
        if (!DateUtil.isIn(new Date(), entity.getEffectiveTime(), entity.getDeadTime())) {
            return Response.error(ResponseEnum.FORBIDDEN);
        }
        return Response.success("查询成功", entity);
    }
}
