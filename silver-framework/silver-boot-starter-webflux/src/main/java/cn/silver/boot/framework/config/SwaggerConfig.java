package cn.silver.boot.framework.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class SwaggerConfig {
    /**
     * Api docket.
     *
     * @return the docket
     */
    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**")
                .build();
    }

    /**
     * Open api open api.
     *
     * @return the open api
     */
    @Bean
    public OpenAPI openApi() {
        Components components = this.components();
        return new OpenAPI().info(apiInfo())
                .components(components)
                // 配置接口访问地址
                .servers(Collections.singletonList(new Server().url("http://127.0.0.1:8080")))
                // 配置认证
                .security(this.security(components));
    }
    /**
     * api文档的详细信息函数,注意这里的注解引用的是哪个
     *
     * @return
     */
    private Info apiInfo() {
        return new Info().title("测试api").description("测试api管理").version("1.0")
                .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html"));
    }
    private Components components() {
        Components components = new Components()
                .addSecuritySchemes("Bearer Authorization", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"))
                .addSecuritySchemes("Basic Authorization", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic"));
        components.addSecuritySchemes("header",new SecurityScheme());
        return components;
    }

    private List<SecurityRequirement> security(Components components) {
        return components.getSecuritySchemes().keySet()
                .stream()
                .map(key -> new SecurityRequirement().addList(key))
                .collect(Collectors.toList());
    }
}
