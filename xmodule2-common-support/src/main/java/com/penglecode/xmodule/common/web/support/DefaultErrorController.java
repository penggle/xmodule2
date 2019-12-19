package com.penglecode.xmodule.common.web.support;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.penglecode.xmodule.common.support.ExceptionDescriptor;
import com.penglecode.xmodule.common.support.ExceptionDescriptorResolver;

public class DefaultErrorController extends BasicErrorController {

	private final ErrorAttributes errorAttributes;
	
	public DefaultErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties,
			List<ErrorViewResolver> errorViewResolvers) {
		super(errorAttributes, errorProperties, errorViewResolvers);
		this.errorAttributes = errorAttributes;
	}

	public DefaultErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties) {
		super(errorAttributes, errorProperties);
		this.errorAttributes = errorAttributes;
	}

	@RequestMapping(produces = "text/html")
	public ModelAndView errorHtml(HttpServletRequest request,
			HttpServletResponse response) {
		HttpStatus status = getStatus(request);
		Map<String, Object> model = Collections.unmodifiableMap(getErrorAttributes(
				request, isIncludeStackTrace(request, MediaType.TEXT_HTML)));
		response.setStatus(status.value());
		ModelAndView modelAndView = resolveErrorView(request, response, status, model);
		return (modelAndView != null) ? modelAndView : new ModelAndView("error", model);
	}

	@RequestMapping
	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
		Map<String, Object> defaultErrorAttributes = getErrorAttributes(request,
				isIncludeStackTrace(request, MediaType.ALL));
		HttpStatus status = getStatus(request);
		
		Map<String, Object> body = new LinkedHashMap<String,Object>();
		Throwable exception = (Throwable) defaultErrorAttributes.get("exception");
		int code = status.value();
		String message = (String) defaultErrorAttributes.get("error");
		if(exception != null) {
			ExceptionDescriptor em = ExceptionDescriptorResolver.resolveException(exception);
			code = em.getCode();
			message = em.getMessage();
		}
		
		body.put("success", Boolean.FALSE);
		body.put("code", code);
		body.put("message", message);
		body.put("data", null);
		return new ResponseEntity<>(body, status);
	}

	protected ErrorAttributes getErrorAttributes() {
		return errorAttributes;
	}
	
}
