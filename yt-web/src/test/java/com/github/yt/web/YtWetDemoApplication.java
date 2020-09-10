package com.github.yt.web;

import com.github.yt.mybatis.EnableYtMybatis;
import com.github.yt.mybatis.YtMybatisConfig;
import com.github.yt.mybatis.exception.DatabaseExceptionConverter;
import com.github.yt.mybatis.util.SpringContextUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.DispatcherServlet;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication()
@EnableYtWeb
@Import({SpringContextUtils.class,
        YtMybatisConfig.class,
})
@EnableSwagger2
public class YtWetDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(YtWetDemoApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean dispatcherRegistration(DispatcherServlet dispatcherServlet) {
        ServletRegistrationBean reg = new ServletRegistrationBean(dispatcherServlet);
        reg.addUrlMappings("/rest/*");
        reg.setName("rest");
        return reg;
    }
}
