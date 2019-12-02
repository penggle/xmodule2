package com.penglecode.xmodule.common.security.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.penglecode.xmodule.common.boot.config.AbstractSpringConfiguration;
import com.penglecode.xmodule.common.security.consts.SecurityApplicationConstants;

@Configuration
public class DefaultSecurityConfiguration extends AbstractSpringConfiguration {

	/**
	 * spring-security自定义的页面跳转配置
	 */
	@Bean
	@ConfigurationProperties(prefix="spring.security.config")
	public SecurityConfigProperties securityConfigProperties() {
		return new SecurityConfigProperties();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public PasswordEncoder passwordEncoder() {
		return SecurityApplicationConstants.DEFAULT_PASSWORD_ENCODER;
	}
	
}
