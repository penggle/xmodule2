package com.penglecode.xmodule.common.security.oauth2.client.support;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.penglecode.xmodule.common.support.Result;
import com.penglecode.xmodule.common.util.SpringWebMvcUtils;

public class DefaultOAuth2AuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final AuthenticationEntryPoint deletage;
	
	public DefaultOAuth2AuthenticationEntryPoint() {
		this(new BearerTokenAuthenticationEntryPoint());
	}

	public DefaultOAuth2AuthenticationEntryPoint(AuthenticationEntryPoint deletage) {
		super();
		this.deletage = deletage;
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		deletage.commence(request, response, authException);
		if (authException instanceof OAuth2AuthenticationException) {
			OAuth2Error error = ((OAuth2AuthenticationException) authException).getError();
			String message = error.getErrorCode() + ": " + error.getDescription();
			Result<Object> result = Result.failure().code(response.getStatus()).message(message).build();
			SpringWebMvcUtils.writeHttpMessage(response, result, MediaType.APPLICATION_JSON);
		}
	}

}
