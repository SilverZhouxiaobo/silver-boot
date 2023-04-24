package cn.silver.boot.framework.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.view.UrlBasedViewResolver;

/**
 * 通用配置
 *
 * @author hb
 */
@Configuration
public class ResourcesConfig implements WebFluxConfigurer {

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        UrlBasedViewResolver resolver = new UrlBasedViewResolver();
        resolver.setViewNames("forward:" + "/doc.html");
        registry.viewResolver(resolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 本地文件上传路径
        // registry.addResourceHandler(Constants.RESOURCE_PREFIX + "/**")
        // 	.addResourceLocations("file:" + AppConfig.getProfile() + "/");
        // swagger配置
        // registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

//    @Override
//    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
//        // 限制上传文件大小
//        SynchronossPartHttpMessageReader partReader = new SynchronossPartHttpMessageReader();
//        partReader.setMaxParts(1);
//        partReader.setMaxDiskUsagePerPart(100 * 1024L * 1024L);
//        partReader.setEnableLoggingRequestDetails(true);
//
//        MultipartHttpMessageReader multipartReader = new MultipartHttpMessageReader(partReader);
//        multipartReader.setEnableLoggingRequestDetails(true);
//        configurer.defaultCodecs().multipartReader(multipartReader);
//
//        // Enable logging
//        // configurer.defaultCodecs().enableLoggingRequestDetails(true);
//
//    }
}