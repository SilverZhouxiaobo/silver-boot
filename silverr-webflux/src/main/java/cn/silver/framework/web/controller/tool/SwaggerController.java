package cn.silver.framework.web.controller.tool;

import cn.silver.framework.core.controller.BaseController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * swagger 接口
 *
 * @author hb
 */
@Controller
@RequestMapping("/tool/swagger")
public class SwaggerController extends BaseController {
    @GetMapping()
    @PreAuthorize("@ss.hasPermi('tool:swagger:view')")
    public String index() {
        return redirect("/swagger-ui.html");
    }
}
