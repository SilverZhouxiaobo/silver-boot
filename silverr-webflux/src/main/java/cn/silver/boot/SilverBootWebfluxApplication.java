package cn.silver.boot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
@SpringBootApplication
public class SilverBootWebfluxApplication {

    public static void main(String[] args) throws UnknownHostException {
        System.setProperty("spring.devtools.restart.enabled", "false");
        ConfigurableApplicationContext application = SpringApplication.run(SilverBootWebfluxApplication.class, args);
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        log.info("\n----------------------------------------------------------\n\t" +
                "(♥◠‿◠)ﾉﾞ      系统启动成功      ლ(´ڡ`ლ)ﾞ! \n\t" +
                "Local: \t\thttp://localhost:" + port + "/\n\t" +
                "External: \thttp://" + ip + ":" + port + "/\n\t" +
                "Swagger文档: \thttp://" + ip + ":" + port + "/doc.html\n" +
                "----------------------------------------------------------");
    }
}
