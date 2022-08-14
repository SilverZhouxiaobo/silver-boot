package cn.silver.framework.system.controller;

import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.system.domain.SysNotice;
import cn.silver.framework.system.service.ISysNoticeService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公告 信息操作处理
 *
 * @author hb
 */
@RestController
@RequestMapping("/sys/notice")
@Api(tags = {"系统消息管理"})
public class SysNoticeController extends DataController<ISysNoticeService, SysNotice> {

    public SysNoticeController() {
        this.authorize = "sys:notice";
        this.title = "系统消息管理";
    }

    /**
     * 获取首页展示通知公告数据
     *
     * @return
     */
    @GetMapping("/getNocticesData")
    public Response getNotciesData() {
        SysNotice sysNotice = new SysNotice();
        //设置类型为通知
        sysNotice.setNoticeType("1");
        PageHelper.startPage(1, 5);
        List<SysNotice> notification = baseService.selectList(sysNotice);
        sysNotice.setNoticeType("2");
        PageHelper.startPage(1, 5);
        List<SysNotice> announcement = baseService.selectList(sysNotice);
        Map<String, List<SysNotice>> map = new HashMap<>();
        map.put("notification", notification);
        map.put("announcement", announcement);
        return Response.success(map);
    }
}
