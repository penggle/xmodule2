package com.penglecode.xmodule.common.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.http.MediaType;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.google.common.io.CharStreams;
import com.penglecode.xmodule.common.consts.GlobalConstants;
import com.penglecode.xmodule.common.exception.ApplicationRuntimeException;

/**
 * 有关HTTP请求的工具类
 * 
 * @author	  	pengpeng
 * @date	  	2014年10月17日 下午7:27:18
 * @version  	1.0
 */
public class HttpServletUtils {

	/**
	 * 从请求中读取请求体
	 * @param request
	 * @return
	 */
	public static String getRequestBody(HttpServletRequest request) {
		return getRequestBody(request, GlobalConstants.DEFAULT_CHARSET);
	}
	
	/**
	 * 从请求中读取请求体
	 * @param request
	 * @return
	 */
	public static String getRequestBody(HttpServletRequest request, String charset) {
		try {
			return CharStreams.toString(new InputStreamReader(request.getInputStream(), charset));
		} catch (IOException e) {
			throw new ApplicationRuntimeException(e);
		}
	}
	
	/**
	 * 获取请求的完整URL
	 * @param request
	 * @param includeQueryString
	 * @return
	 */
	public static String getRequestURL(HttpServletRequest request, boolean includeQueryString) {
		StringBuffer url = request.getRequestURL();
        if(includeQueryString && !StringUtils.isEmpty(request.getQueryString())){
        	url.append("?" + request.getQueryString());
		}
        return url.toString();
	}
	
	/**
	 * 获取请求的相对路径(包括contextPath)
	 * @param request
	 * @param includeQueryString
	 * @return
	 */
	public static String getRequestURI(HttpServletRequest request, boolean includeQueryString) {
		String url = request.getRequestURI();
		if(includeQueryString && !StringUtils.isEmpty(request.getQueryString())){
			url += "?" + request.getQueryString();
		}
		return url;
	}
	
	/**
	 * 获取请求的相对路径(不包括contextPath)
	 * @param request
	 * @param includeQueryString
	 * @return
	 */
	public static String getRequestPath(HttpServletRequest request, boolean includeQueryString) {
		String uri = getRequestURI(request, includeQueryString);
		String contextPath = request.getContextPath();
		if(!StringUtils.isEmpty(contextPath) || "/".equals(contextPath)) {
			uri = uri.substring(contextPath.length());
		}
		return uri;
	}
	
	/**
	 * 判断来的请求是否是ajax异步请求
	 * @param request
	 * @return
	 */
    public static boolean isAjaxRequest(HttpServletRequest request) {
    	return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
    
    /**
     * 异步输出响应
     * @param response
     * @param contentType
     * @param content
     * @throws IOException
     */
    public static void asynOutputResponse(HttpServletResponse response, MediaType contentType, String content) throws IOException  {
    	response.setContentType(contentType.toString());
		response.setCharacterEncoding(contentType.getCharset().toString());    
		PrintWriter out = response.getWriter();
		out.print(content);
		out.flush();
		out.close();
    }
    
    /**
     * 根据字符串类型的contentType来获取content-type的枚举对象org.springframework.http.MediaType
     * @param contentType
     * @return
     */
    public static MediaType getContentType(String contentType) {
    	try {
			if(!StringUtils.isEmpty(contentType)){
				return MediaType.valueOf(contentType);
			}
		} catch (Exception e) {
		}
		return null;
	}

    /**
	 * 获取ContentCachingRequestWrapper
	 * (考虑到Wrapper是可以重复的,故需要递归获取)
	 * @param response
	 * @return
	 */
	public static ContentCachingRequestWrapper getContentCachingRequestWrapper(ServletRequest request) {
		if(request instanceof ContentCachingRequestWrapper){
			return (ContentCachingRequestWrapper) request;
		}else if(request instanceof HttpServletRequestWrapper) {
			HttpServletRequestWrapper requestToUse = (HttpServletRequestWrapper) request;
			return getContentCachingRequestWrapper(requestToUse.getRequest());
		}
		return null;
	}
	
	/**
	 * 获取ContentCachingResponseWrapper
	 * (考虑到Wrapper是可以重复的,故需要递归获取)
	 * @param response
	 * @return
	 */
	public static ContentCachingResponseWrapper getContentCachingResponseWrapper(ServletResponse response) {
		if(response instanceof ContentCachingResponseWrapper){
			return (ContentCachingResponseWrapper) response;
		}else if(response instanceof HttpServletResponseWrapper) {
			HttpServletResponseWrapper responseToUse = (HttpServletResponseWrapper) response;
			return getContentCachingResponseWrapper(responseToUse.getResponse());
		}
		return null;
	}
	
	/**
	 * 判断指定request是否被ContentCachingRequestWrapper所包裹
	 * @param request
	 * @return
	 */
	public static boolean isContentCachingRequest(ServletRequest request) {
		return getContentCachingRequestWrapper(request) != null;
	}
	
	/**
	 * 判断指定request是否被ContentCachingResponseWrapper所包裹
	 * @param response
	 * @return
	 */
	public static boolean isContentCachingResponse(ServletResponse response) {
		return getContentCachingResponseWrapper(response) != null;
	}
	
	/**
	 * 获取请求的CharacterEncoding
	 * @param request
	 * @param defaultCharset
	 * @return
	 */
	public static String getCharacterEncoding(HttpServletRequest request, String defaultCharset) {
		String charset = request.getCharacterEncoding();
		if(StringUtils.isEmpty(charset)){
			MediaType contentType = getContentType(request.getContentType());
			if(contentType != null) {
				charset = contentType.getCharset().name();
			}
		}
		return StringUtils.defaultIfEmpty(charset, GlobalConstants.DEFAULT_CHARSET);
	}
	
	/**
	 * 获取响应的CharacterEncoding
	 * @param request
	 * @param defaultCharset
	 * @return
	 */
	public static String getCharacterEncoding(HttpServletResponse response, String defaultCharset) {
		String charset = response.getCharacterEncoding();
		if(StringUtils.isEmpty(charset)){
			MediaType contentType = getContentType(response.getContentType());
			if(contentType != null) {
				charset = contentType.getCharset().name();
			}
		}
		return StringUtils.defaultIfEmpty(charset, GlobalConstants.DEFAULT_CHARSET);
	}
    
}
