package com.github.yt.web.test;

import com.github.yt.mybatis.conf.YtMybatisProperties;
import com.github.yt.mybatis.util.SpringContextUtils;
import com.github.yt.web.unittest.EnableYtWebTest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@Import({SpringContextUtils.class,
        YtMybatisProperties.class,
})
@EnableSwagger2
@EnableYtWebTest
public class YtWebTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(YtWebTestApplication.class, args);
    }
}
