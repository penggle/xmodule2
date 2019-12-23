package com.penglecode.xmodule.security.oauth2.examples.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.penglecode.xmodule.common.security.oauth2.client.support.DefaultOAuth2AuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class OAuth2ResourceServerConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
				.antMatcher("/api/**") //指定API资源访问安全管辖路径
				.authorizeRequests()
				.antMatchers("/api/server/nowtime").permitAll()
				.antMatchers("/api/**").access("hasAnyAuthority('SCOPE_user', 'SCOPE_app')")
			.and()
				.oauth2ResourceServer()
					.authenticationEntryPoint(new DefaultOAuth2AuthenticationEntryPoint())
					.jwt();
	}
	
}
