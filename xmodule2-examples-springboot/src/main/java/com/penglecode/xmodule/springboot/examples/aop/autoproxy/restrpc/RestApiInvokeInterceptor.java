package com.penglecode.xmodule.springboot.examples.aop.autoproxy.restrpc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.penglecode.xmodule.common.util.ArrayUtils;
import com.penglecode.xmodule.common.util.FileUtils;
import com.penglecode.xmodule.common.util.ReflectionUtils;
import com.penglecode.xmodule.common.util.StringUtils;

public class RestApiInvokeInterceptor implements MethodInterceptor {

	private ParameterNameDiscoverer defaultarameterNameDiscoverer = new DefaultParameterNameDiscoverer();
	
	private MediaType defaultContentType = MediaType.APPLICATION_JSON;
	
	private RestTemplate restTemplate = new RestTemplate();
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		Object proxy = ((ReflectiveMethodInvocation) invocation).getProxy();
		Object aopProxy = Proxy.getInvocationHandler(proxy);
		Class<?> proxyInterface = AopProxyUtils.proxiedUserInterfaces(proxy)[0];
		if(ReflectionUtils.isObjectMethod(method)) {
			if(ReflectionUtils.isHashCodeMethod(method)) {
				return aopProxy.hashCode();
			} else if (ReflectionUtils.isToStringMethod(method)) {
				int hashCode = aopProxy.hashCode();
				return proxyInterface.getName() + "@" + Integer.toHexString(hashCode);
			} else if (ReflectionUtils.isEqualsMethod(method)) {
				Object other = invocation.getArguments()[0];
				return aopProxy.equals(other);
			} else if (ReflectionUtils.isGetClassMethod(method)) {
				return proxyInterface;
			} else {
				throw new IllegalStateException("Method invocation not supported!");
			}
		} else {
			return doInvoke(invocation);
		}
	}
	
	protected Object doInvoke(MethodInvocation invocation) throws Throwable {
		Method restApiMethod = invocation.getMethod();
		Class<?> restApiClass = restApiMethod.getDeclaringClass();
		RestApi restApiAnno = restApiClass.getAnnotation(RestApi.class);
		
		RequestMapping restApiClassRequestMapping = AnnotatedElementUtils.findMergedAnnotation(restApiClass, RequestMapping.class);
		RequestMapping restApiMethodRequestMapping = AnnotatedElementUtils.findMergedAnnotation(restApiMethod, RequestMapping.class);
		
		RequestMappingObject requestMapping = mergeRequestMapping(restApiClassRequestMapping, restApiMethodRequestMapping);
		RequestParameterObject requestParameter = extractRequestParameter(invocation);
		Type returnType = restApiMethod.getGenericReturnType();
		
		return executeRequest(restApiAnno, requestMapping, requestParameter, returnType);
	}
	
	protected Object executeRequest(RestApi restApiAnno, RequestMappingObject requestMapping, RequestParameterObject requestParameter, Type returnType) {
		String url = FileUtils.joinPaths(restApiAnno.clientUrl(), requestMapping.getPath());
		
		HttpEntity<?> requestEntity = HttpEntity.EMPTY;
		
		if(requestParameter.getRequestBody() != null) {
			HttpHeaders headers = new HttpHeaders();
			if(!ArrayUtils.isEmpty(requestMapping.getConsumes())) {
				headers.setContentType(MediaType.valueOf(requestMapping.getConsumes()[0]));
			} else {
				headers.setContentType(defaultContentType);
			}
			requestEntity = new HttpEntity<Object>(requestParameter.getRequestBody(), headers);
		}
		
		UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(url);
		Map<String,Object> urlParameters = new HashMap<String,Object>();
		if(!CollectionUtils.isEmpty(requestParameter.getPathParams())) {
			urlParameters.putAll(requestParameter.getPathParams());
		}
		if(!CollectionUtils.isEmpty(requestParameter.getRequestParams())) {
			Set<String> queryParamNames = requestParameter.getRequestParams().keySet();
			for(String paramName : queryParamNames) {
				urlBuilder.query(paramName + "={" + paramName + "}");
			}
			urlParameters.putAll(requestParameter.getRequestParams());
		}
		
		url = urlBuilder.build().toString();
		
		ResponseEntity<Object> response = getRestTemplate().exchange(url, HttpMethod.valueOf(requestMapping.getMethod().name()), requestEntity, ParameterizedTypeReference.forType(returnType), urlParameters);
		return response.getBody();
	}
	
	protected RequestParameterObject extractRequestParameter(MethodInvocation invocation) {
		Method method = invocation.getMethod();
		int methodArgCount = method.getParameterCount();
		RequestParameterObject parameter = null;
		if(methodArgCount > 0) {
			Object[] methodArgValues = invocation.getArguments();
			String[] methodArgNames = defaultarameterNameDiscoverer.getParameterNames(method);
			
			Map<String,Object> pathParams = new LinkedHashMap<String,Object>();
			Map<String,Object> requestParams = new LinkedHashMap<String,Object>();
			Object requestBody = null;
			
			Annotation[][] allParamAnnos = method.getParameterAnnotations();
			for(int i = 0; i < methodArgCount; i++) {
				Annotation[] paramAnnos = allParamAnnos[i];
				if(!ArrayUtils.isEmpty(paramAnnos)) {
					for(Annotation paramAnno : paramAnnos) {
						if(paramAnno instanceof PathVariable) {
							String paramName = StringUtils.defaultIfEmpty(((PathVariable) paramAnno).name(), methodArgNames[i]);
							pathParams.put(paramName, methodArgValues[i]);
							break;
						}
						if(paramAnno instanceof RequestParam) {
							String paramName = StringUtils.defaultIfEmpty(((RequestParam) paramAnno).name(), methodArgNames[i]);
							requestParams.put(paramName, methodArgValues[i]);
							break;
						}
						if(requestBody == null && paramAnno instanceof RequestBody) {
							requestBody = methodArgValues[i];
							break;
						}
					}
				} else {
					requestParams.put(methodArgNames[i], methodArgValues[i]); //默认任何不加注解的参数为@RequestParam注解参数
				}
			}
			
			parameter = new RequestParameterObject();
			parameter.setPathParams(pathParams);
			parameter.setRequestParams(requestParams);
			parameter.setRequestBody(requestBody);
		}
		return parameter;
	}

	protected RequestMappingObject mergeRequestMapping(RequestMapping classRequestMapping, RequestMapping methodRequestMapping) {
		RequestMappingObject mapping = new RequestMappingObject();
		
		String path = methodRequestMapping.path()[0];
		if(classRequestMapping != null && !ArrayUtils.isEmpty(classRequestMapping.path()) && !StringUtils.isEmpty(classRequestMapping.path()[0])) {
			path = FileUtils.joinPaths(classRequestMapping.path()[0], path);
		}
		mapping.setPath(path);
		
		RequestMethod method = RequestMethod.GET;
		if(methodRequestMapping != null && !ArrayUtils.isEmpty(methodRequestMapping.method())) {
			method = methodRequestMapping.method()[0];
		} else if (classRequestMapping != null && !ArrayUtils.isEmpty(classRequestMapping.method())) {
			method = classRequestMapping.method()[0];
		}
		mapping.setMethod(method);
		
		Set<String> params = new HashSet<String>();
		if (classRequestMapping != null && !ArrayUtils.isEmpty(classRequestMapping.params())) {
			params.addAll(Arrays.asList(classRequestMapping.params()));
		}
		if (methodRequestMapping != null && !ArrayUtils.isEmpty(methodRequestMapping.params())) {
			params.addAll(Arrays.asList(methodRequestMapping.params()));
		}
		mapping.setParams(params.toArray(new String[0]));
		
		Set<String> headers = new HashSet<String>();
		if (classRequestMapping != null && !ArrayUtils.isEmpty(classRequestMapping.headers())) {
			headers.addAll(Arrays.asList(classRequestMapping.headers()));
		}
		if (methodRequestMapping != null && !ArrayUtils.isEmpty(methodRequestMapping.headers())) {
			headers.addAll(Arrays.asList(methodRequestMapping.headers()));
		}
		mapping.setHeaders(headers.toArray(new String[0]));
		
		Set<String> consumes = new HashSet<String>();
		if (classRequestMapping != null && !ArrayUtils.isEmpty(classRequestMapping.consumes())) {
			consumes.addAll(Arrays.asList(classRequestMapping.consumes()));
		}
		if (methodRequestMapping != null && !ArrayUtils.isEmpty(methodRequestMapping.consumes())) {
			consumes.addAll(Arrays.asList(methodRequestMapping.consumes()));
		}
		mapping.setConsumes(consumes.toArray(new String[0]));
		
		Set<String> produces = new HashSet<String>();
		if (classRequestMapping != null && !ArrayUtils.isEmpty(classRequestMapping.produces())) {
			produces.addAll(Arrays.asList(classRequestMapping.produces()));
		}
		if (methodRequestMapping != null && !ArrayUtils.isEmpty(methodRequestMapping.produces())) {
			produces.addAll(Arrays.asList(methodRequestMapping.produces()));
		}
		mapping.setProduces(produces.toArray(new String[0]));
		
		return mapping;
	}

	protected ParameterNameDiscoverer getDefaultarameterNameDiscoverer() {
		return defaultarameterNameDiscoverer;
	}

	public void setDefaultarameterNameDiscoverer(ParameterNameDiscoverer defaultarameterNameDiscoverer) {
		this.defaultarameterNameDiscoverer = defaultarameterNameDiscoverer;
	}

	protected RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	protected MediaType getDefaultContentType() {
		return defaultContentType;
	}

	public void setDefaultContentType(MediaType defaultContentType) {
		this.defaultContentType = defaultContentType;
	}
	
}
