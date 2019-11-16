package com.penglecode.xmodule.common.config;

import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * spring-boot-starter-actuator启用安全认证
 * 
 * @author 	pengpeng
 * @date	2018年9月6日 下午4:02:37
 */
@Configuration
@EnableWebFluxSecurity
public class SpringActuatorSecurityConfiguration {

	@Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		http.authorizeExchange()
			.matchers(EndpointRequest.to("info", "health")).permitAll()
			.matchers(EndpointRequest.toAnyEndpoint()).hasRole("ACTUATOR")
			.anyExchange().permitAll()
		.and().cors()
		.and().httpBasic()
		.and().csrf().disable();
		return http.build();
	}
	
}
