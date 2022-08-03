package cn.silver.framework;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 启动程序
 *
 * @author hb
 */
@Slf4j
@EnableRabbit
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = {"cn.hb.software.gacim"})
public class GacimApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(GacimApplication.class);
    }

    public static void main(String[] args) throws UnknownHostException {
        System.setProperty("spring.devtools.restart.enabled", "false");
        ConfigurableApplicationContext application = SpringApplication.run(GacimApplication.class, args);
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        log.info("\n----------------------------------------------------------\n\t" +
                "(♥◠‿◠)ﾉﾞ  投资者关系管理系统启动成功   ლ(´ڡ`ლ)ﾞ! \n\t" +
                "Local: \t\thttp://localhost:" + port + "/\n\t" +
                "External: \thttp://" + ip + ":" + port + "/\n\t" +
                "Swagger文档: \thttp://" + ip + ":" + port + "/doc.html\n" +
                "----------------------------------------------------------");
    }
}
