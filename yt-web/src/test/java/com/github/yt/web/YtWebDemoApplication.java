package com.github.yt.web;

import com.github.yt.mybatis.YtMybatisConfig;
import com.github.yt.mybatis.util.SpringContextUtils;
import com.github.yt.web.unittest.EnableYtWebTest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@EnableYtWeb
@Import({SpringContextUtils.class,
        YtMybatisConfig.class,
})
@EnableSwagger2
public class YtWebDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(YtWebDemoApplication.class, args);
    }
}
