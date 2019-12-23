package com.penglecode.xmodule.common.util;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
/**
 * SpringMVC的工具类
 * 
 * @author 	pengpeng
 * @date   		2017年5月13日 上午10:08:47
 * @version 	1.0
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SpringWebMvcUtils {
	
	/**
	 * 获取绑定在当前线程上下文下的HttpServletRequest对象
	 * (通过{@link org.springframework.web.context.request.RequestContextListener}实现)
	 * @return
	 */
	public static HttpServletRequest getCurrentHttpServletRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}
	
	/**
	 * 获取绑定在当前线程上下文下的HttpServletRequest对象
	 * (通过{@link org.springframework.web.context.request.RequestContextListener}实现)
	 * @return
	 */
	public static HttpServletResponse getCurrentHttpServletResponse() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
	}
	
	/**
	 * 获取当前线程上下文下的HttpSession对象
	 * @return
	 */
	public static HttpSession getCurrentHttpSession() {
		return getCurrentHttpServletRequest().getSession();
	}
	
	/**
	 * 判断请求是否是异步请求
	 * @param request
	 * @param handler
	 * @return
	 */
	public static boolean isAsyncRequest(HttpServletRequest request, Object handler){
		boolean isAsync = false;
		if(handler instanceof HandlerMethod){
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			isAsync = handlerMethod.hasMethodAnnotation(ResponseBody.class);
			if(!isAsync){
				Class<?> controllerClass = handlerMethod.getBeanType();
				isAsync = controllerClass.isAnnotationPresent(ResponseBody.class) || controllerClass.isAnnotationPresent(RestController.class);
			}
			if(!isAsync){
				isAsync = ResponseEntity.class.equals(handlerMethod.getMethod().getReturnType());
			}
		}
		if(!isAsync){
			isAsync = HttpServletUtils.isAjaxRequest(request);
		}
		return isAsync;
	}
	
	/**
	 * 通过SpringMVC的HttpMessageConverter来输出响应
	 * @see #AbstractMessageConverterMethodProcessor.writeWithMessageConverters
	 * @param response				- 当前HttpServletResponse响应，如果过为空则从SpringMVC上下文中获取
	 * @param responseBody			- 输出的响应体
	 * @param responseContentType	- 输出的响应媒体类型(例如：application/json)
	 * @throws IOException
	 */
	public static void writeHttpMessage(HttpServletResponse response, Object responseBody, MediaType responseContentType) throws IOException {
		if(responseBody != null) {
			if(response == null) {
				response = getCurrentHttpServletResponse();
			}
			Assert.notNull(response, "Parameter 'response' must be required!");
			HttpMessageConverters httpMessageConverters = SpringUtils.getBean(HttpMessageConverters.class);
			for(HttpMessageConverter httpMessageConverter : httpMessageConverters) {
				if(httpMessageConverter.canWrite(responseBody.getClass(), responseContentType)) {
					httpMessageConverter.write(responseBody, responseContentType, new ServletServerHttpResponse(response));
					return;
				}
			}
		}
	}
	
}
