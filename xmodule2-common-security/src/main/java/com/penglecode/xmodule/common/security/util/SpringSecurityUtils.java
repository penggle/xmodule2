package com.penglecode.xmodule.common.security.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.WebAttributes;

/**
 * spring-security工具类
 * 
 * @author 	pengpeng
 * @date	2019年1月16日 下午2:38:43
 */
public class SpringSecurityUtils {

	/**
	 * 获取认证(登录)异常
	 * @param request
	 * @return
	 */
	public static Exception getAuthenticationException(HttpServletRequest request) {
		String key = WebAttributes.AUTHENTICATION_EXCEPTION;
		Exception exception = null;
		if(exception == null) {
			exception = (Exception) request.getAttribute(key);
		}
		if(exception == null) {
			exception = (Exception) request.getSession().getAttribute(key);
			if(exception != null) {
				request.getSession().removeAttribute(key);
			}
		}
		return exception;
	}
	
}
