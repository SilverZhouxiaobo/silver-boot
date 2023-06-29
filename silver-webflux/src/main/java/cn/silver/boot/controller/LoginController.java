package cn.silver.boot.controller;

import cn.silver.boot.framework.domain.Response;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouxiaobo
 */
@RestController
@RequestMapping("/login")
@Tag(name = "系统登录接口", description = "系统登录接口，用于系统登录认证")
public class LoginController {
    @PostMapping("/")
    @Operation(summary = "用户登录", description = "系统用户登录")
    public Response<JSONObject> login(@RequestBody JSONObject params) {
        //申请的接口地址
        //处理分析的文本，作为params参数传入
        Map<String, Object> paramsMap = new HashMap<>(1);
        //参数传入要处理分析的文本
        paramsMap.put("text", params);
        //执行HanLP接口，result为返回的结果
        return Response.success();
    }
}
