package com.penglecode.xmodule.security.oauth2.examples.support;

/**
 * https://api.apiopen.top上的开放接口结果报文
 * 
 * @param <T>
 * @author 	pengpeng
 * @date	2019年11月14日 上午10:06:02
 */
public class OpenApiResult<T> {

	private Integer code;
	
	private String message;
	
	private T result;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}
	
}
