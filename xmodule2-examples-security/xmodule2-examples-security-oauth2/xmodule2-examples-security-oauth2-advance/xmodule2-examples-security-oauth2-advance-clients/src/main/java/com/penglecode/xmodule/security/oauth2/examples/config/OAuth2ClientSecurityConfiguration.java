package com.penglecode.xmodule.security.oauth2.examples.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.penglecode.xmodule.common.security.consts.SecurityApplicationConstants;
import com.penglecode.xmodule.common.security.oauth2.client.util.OAuth2AccessTokenResponseClientUtils;

@Configuration
@EnableWebSecurity
public class OAuth2ClientSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return SecurityApplicationConstants.DEFAULT_PASSWORD_ENCODER;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.antMatcher("/**")
			.authorizeRequests()
				.anyRequest().permitAll()
				.and()
			.formLogin()
				.loginPage("/login").failureUrl("/login").defaultSuccessUrl("/index").permitAll()
				.and()
			.logout()
				.logoutUrl("/logout").permitAll()
			.and()
				.csrf().disable()
			.oauth2Client()
				.authorizationCodeGrant().accessTokenResponseClient(authorizationCodeTokenResponseClient());
	}
	
	protected DefaultAuthorizationCodeTokenResponseClient authorizationCodeTokenResponseClient() {
		DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
		accessTokenResponseClient.setRestOperations(OAuth2AccessTokenResponseClientUtils.createDefaultRestTemplate());
		return accessTokenResponseClient;
	}
	
	@Bean
    public UserDetailsService users() {
        UserDetails user = User.withUsername("user1")
            .password("123456")
            .roles("USER")
            .build();
        return new InMemoryUserDetailsManager(user);
    }
	
}
