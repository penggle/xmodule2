package com.penglecode.xmodule.common.web.support;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

public class DefaultErrorAttributes extends org.springframework.boot.web.servlet.error.DefaultErrorAttributes {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultErrorAttributes.class);
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		LOGGER.error(ex.getMessage(), ex);
		return super.resolveException(request, response, handler, ex);
	}

	@Override
	public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
		Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);
		errorAttributes.put("exception", getError(webRequest));
		return errorAttributes;
	}

}
