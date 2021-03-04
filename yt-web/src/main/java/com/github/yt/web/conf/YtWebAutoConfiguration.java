package com.github.yt.web.conf;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * starter
 *
 * @author sheng
 * @version 1.0
 * @date 2021-03-04 22:41
 */
@Configuration
//@EnableConfigurationProperties(YtWebProperties.class)
@ConditionalOnProperty(name = "yt.web.enable", matchIfMissing = true)
public class YtWebAutoConfiguration {

//	@Bean
//	@ConditionalOnMissingBean
//	public PackageResponseBodyAdvice packageResponseBodyAdvice(YtWebProperties ytWebProperties) {
//		return new PackageResponseBodyAdvice(ytWebProperties);
//	}
//	@Bean
//	@ConditionalOnMissingBean
//	public YtWebMvcConfigurer ytWebMvcConfigurer() {
//		return new YtWebMvcConfigurer();
//	}
//	@Bean
//	@ConditionalOnMissingBean
//	public QueryControllerAspect queryControllerAspect(YtWebProperties ytWebProperties) {
//		return new QueryControllerAspect();
//	}
//	@Bean
//	@ConditionalOnMissingBean
//	public ValidatorExceptionConverter validatorExceptionConverter() {
//		return new ValidatorExceptionConverter();
//	}
//
//	@Bean
//	@ConditionalOnMissingBean
//	public KnowExceptionConverter knowExceptionConverter() {
//		return new KnowExceptionConverter();
//	}
//
//	@Bean
//	@ConditionalOnMissingBean
//	public SpringContextUtils springContextUtils() {
//		return new SpringContextUtils();
//	}



}
