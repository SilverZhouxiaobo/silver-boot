package cn.silver.framework.ureport;

import com.bstek.ureport.console.UReportServlet;
import com.bstek.ureport.definition.datasource.BuildinDatasource;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.sql.DataSource;
import java.sql.Connection;


@Configuration
//不加项目能够启动但是会导致加载数据源报错或加载不了
@ImportResource("classpath:ureport-console-context.xml")
public class UReportConfig implements BuildinDatasource {
    @Autowired
    private DataSource datasource;

    //定义ureport的启动servlet
    @Bean
    @SuppressWarnings("unchecked")
    public ServletRegistrationBean ureportServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new UReportServlet());
        servletRegistrationBean.addUrlMappings("/ureport/*");
        return servletRegistrationBean;
    }

    @Override
    public String name() {
        return "内部数据源";
    }

    @SneakyThrows
    @Override
    public Connection getConnection() {
        return datasource.getConnection();
    }
}
