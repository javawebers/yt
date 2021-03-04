package com.github.yt.mybatis.conf;

import com.github.yt.mybatis.exception.DatabaseExceptionConverter;
import com.github.yt.mybatis.util.SpringContextUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * starter
 *
 * @author sheng
 * @version 1.0
 * @date 2021-03-04 22:41
 */
@Configuration
@EnableConfigurationProperties(YtMybatisProperties.class)
@ConditionalOnProperty(name = "yt.mybatis.enable", matchIfMissing = true)
public class YtMybatisAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public SpringContextUtils ytMybatisSpringContextUtils() {
		return new SpringContextUtils();
	}

	@Bean
	@ConditionalOnMissingBean
	public DatabaseExceptionConverter databaseExceptionConverter() {
		return new DatabaseExceptionConverter();
	}


}
