package cn.silver.boot.controller;

import cn.silver.boot.framework.domain.Response;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/login")
@Tag(name = "系统登录接口", description = "系统登录接口，用于系统登录认证")
public class LoginController {
    @GetMapping("/")
    @Operation(summary = "通用接口调用",
            description = "nlp通用接口调用实例",
            parameters = {
                    @Parameter(name = "url", description = "接口地址"),
                    @Parameter(name = "token", description = "请求token"),
                    @Parameter(name = "params", description = "请求参数")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "返回提取成功的命名实体"),
                    @ApiResponse(responseCode = "400", description = "返回400时候错误的原因")
            }
    )
    public Response<JSONObject> common(@RequestParam String url, @RequestParam String token, @RequestParam String params) {
        //申请的接口地址
        //处理分析的文本，作为params参数传入
        Map<String, Object> paramsMap = new HashMap<>(1);
        //参数传入要处理分析的文本
        paramsMap.put("text", params);
        //执行HanLP接口，result为返回的结果
        return Response.success();
    }
}
