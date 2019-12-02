package com.penglecode.xmodule.common.web.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * HttpAccessExceptionHandler基类
 * 
 * @author 	pengpeng
 * @date	2019年2月14日 下午3:00:00
 */
public abstract class AbstractHttpAccessExceptionHandler implements HttpAccessExceptionHandler, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHttpAccessExceptionHandler.class);
	
	/**
	 * 默认的响应体类型,默认application/json
	 */
	private static final MediaType DEFAULT_RESPONSE_CONTENT_TYPE = MediaType.APPLICATION_JSON;
	
	/**
	 * 强制指定的响应内容类型
	 */
	private MediaType responseContentType;
	
	private HttpMessageConverters httpMessageConverters;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if(httpMessageConverters == null) {
			httpMessageConverters = createDefaultHttpMessageConverters();
		}
	}

	@Override
	public void handleException(HttpServletRequest request, HttpServletResponse response, Exception exception, Object attachment) {
		try {
			ResponseEntity<?> responseEntity = createResponseEntity(request, response, exception, attachment);
			ServletWebRequest webRequest = new ServletWebRequest(request, response);
			HttpInputMessage inputMessage = createHttpInputMessage(webRequest);
			HttpOutputMessage outputMessage = createHttpOutputMessage(webRequest);
			if (outputMessage instanceof ServerHttpResponse) {
				((ServerHttpResponse) outputMessage).setStatusCode(responseEntity.getStatusCode());
			}
			HttpHeaders entityHeaders = responseEntity.getHeaders();
			if (!entityHeaders.isEmpty()) {
				outputMessage.getHeaders().putAll(entityHeaders);
			}
			Object body = responseEntity.getBody();
			if (body != null) {
				writeWithMessageConverters(body, inputMessage, outputMessage);
			} else {
				outputMessage.getBody(); // nobody, only flush headers
			}
		} catch (Exception e) {
			LOGGER.error(String.format("Handling http access exception occurred an error: %s", e.getMessage()), e);
		}
	}
	
	/**
	 * 创建响应体
	 * @param request
	 * @param response
	 * @param exception
	 * @param attachment
	 * @return
	 */
	protected abstract ResponseEntity<?> createResponseEntity(HttpServletRequest request, HttpServletResponse response, Exception exception, Object attachment) throws Exception;

	/**
	 * 使用适当的HttpMessageConverters输出响应体
	 * @param returnValue
	 * @param inputMessage
	 * @param outputMessage
	 * @throws IOException
	 * @throws HttpMediaTypeNotAcceptableException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void writeWithMessageConverters(Object returnValue, HttpInputMessage inputMessage,
			HttpOutputMessage outputMessage) throws IOException, HttpMediaTypeNotAcceptableException {
		List<MediaType> acceptedMediaTypes = resolveAcceptedMediaTypes(inputMessage, outputMessage);
		MediaType.sortByQualityValue(acceptedMediaTypes);
		Class<?> returnValueType = returnValue.getClass();
		List<MediaType> allSupportedMediaTypes = new ArrayList<MediaType>();
		for (MediaType acceptedMediaType : acceptedMediaTypes) {
			for (HttpMessageConverter messageConverter : httpMessageConverters) {
				if (messageConverter.canWrite(returnValueType, acceptedMediaType)) {
					messageConverter.write(returnValue, acceptedMediaType, outputMessage);
					LOGGER.debug("Written [" + returnValue + "] as \"" + acceptedMediaType + "\" using [" + messageConverter + "]");
					return;
				}
			}
		}
		for (HttpMessageConverter messageConverter : httpMessageConverters) {
			allSupportedMediaTypes.addAll(messageConverter.getSupportedMediaTypes());
		}
		throw new HttpMediaTypeNotAcceptableException(allSupportedMediaTypes);
	}
	
	/**
	 * 解析备选的响应输出类型
	 * @param inputMessage
	 * @param outputMessage
	 * @return
	 */
	protected List<MediaType> resolveAcceptedMediaTypes(HttpInputMessage inputMessage, HttpOutputMessage outputMessage) {
		List<MediaType> acceptedMediaTypes = new ArrayList<MediaType>();
		if(getResponseContentType() != null) {
			acceptedMediaTypes.add(getResponseContentType());
		} else {
			MediaType responseContentType = outputMessage.getHeaders().getContentType();
			if(responseContentType == null) {
				acceptedMediaTypes.addAll(inputMessage.getHeaders().getAccept());
				if(acceptedMediaTypes.isEmpty()) {
					acceptedMediaTypes.add(DEFAULT_RESPONSE_CONTENT_TYPE);
				}
			} else {
				acceptedMediaTypes.add(responseContentType);
			}
		}
		return acceptedMediaTypes;
	}
	
	protected HttpMessageConverters createDefaultHttpMessageConverters() {
		return new HttpMessageConverters(new RestTemplate().getMessageConverters());
	}
	
	protected HttpInputMessage createHttpInputMessage(NativeWebRequest webRequest) throws Exception {
		HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
		return new ServletServerHttpRequest(servletRequest);
	}

	protected HttpOutputMessage createHttpOutputMessage(NativeWebRequest webRequest) throws Exception {
		HttpServletResponse servletResponse = (HttpServletResponse) webRequest.getNativeResponse();
		return new ServletServerHttpResponse(servletResponse);
	}
	
	public MediaType getResponseContentType() {
		return responseContentType;
	}

	public void setResponseContentType(MediaType responseContentType) {
		this.responseContentType = responseContentType;
	}

	public HttpMessageConverters getHttpMessageConverters() {
		return httpMessageConverters;
	}

	@Autowired(required=false)
	public void setHttpMessageConverters(HttpMessageConverters httpMessageConverters) {
		this.httpMessageConverters = httpMessageConverters;
	}
	
}
