package com.penglecode.xmodule.keycloak.examples.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * spring-boot-starter-actuator启用安全认证
 * 
 * @author 	pengpeng
 * @date	2018年9月6日 下午4:02:37
 */

@Configuration
@EnableWebSecurity
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SpringActuatorSecurityConfiguration extends WebSecurityConfigurerAdapter {

	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/assets/**");
	}
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
		http.antMatcher("/actuator/**") //指定actuator访问安全管辖路径
			.authorizeRequests()
	        .requestMatchers(EndpointRequest.to("info", "health")).permitAll()
	        .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ACTUATOR")
	    .and()
	        .httpBasic()
	    .and()
	    	.csrf().disable();
    }
	
}
