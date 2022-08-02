package cn.silver.framework.system.controller;


import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.system.domain.SysExternalLink;
import cn.silver.framework.system.service.ISysExternalLinkService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 外链信息Controller
 *
 * @author hb
 * @date 2022-06-13
 */
@Slf4j
@RestController
@Api(tags = {"外链信息"})
@RequestMapping("/sys/external/link")
public class SysExternalLinkController extends DataController<ISysExternalLinkService, SysExternalLink> {

    public SysExternalLinkController() {
        this.authorize = "sys:external:link";
        this.title = "外链信息";
    }

//    @GetMapping("/qrCode/{id}")
//    @ApiOperation(value = "获取二维码", notes = "获取外链对应的二维码")
//    @PreAuthorize("@ss.hasPermi('sys:external:link:query')")
//    @Log(title = "data", businessType = BusinessType.DETAIL)
//    public void getQRCode(@PathVariable String id, HttpServletResponse response) {
//        SysExternalLink link = this.baseService.selectById(id);
//        log.info("codeContent=" + link.getLinkUrl() + "?" + link.getLinkParams());
//        try {
//            /*
//             * 调用工具类生成二维码并输出到输出流中
//             */
//            QRCodeManageUtil.createCodeToOutputStream(link.getLinkUrl() + "?" + link.getLinkParams(), response.getOutputStream());
//            log.info("成功生成二维码!");
//        } catch (IOException e) {
//            log.error("发生错误， 错误信息是：{}！", e.getMessage());
//        }
//    }
}
