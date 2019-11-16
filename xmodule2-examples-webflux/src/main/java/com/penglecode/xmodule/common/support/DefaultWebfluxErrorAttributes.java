package com.penglecode.xmodule.common.support;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import com.google.common.base.Throwables;

public class DefaultWebfluxErrorAttributes extends DefaultErrorAttributes {

	public DefaultWebfluxErrorAttributes() {
		super();
	}

	public DefaultWebfluxErrorAttributes(boolean includeException) {
		super(includeException);
	}

	@Override
	public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
		if(onlyAcceptsTextHtml(request)) {
			return getErrorAttributes4HtmlView(request, includeStackTrace);
		} else {
			return getErrorAttributes4OtherView(request, includeStackTrace);
		}
	}
	
	protected Map<String, Object> getErrorAttributes4HtmlView(ServerRequest request, boolean includeStackTrace) {
		return super.getErrorAttributes(request, includeStackTrace);
	}

	protected Map<String, Object> getErrorAttributes4OtherView(ServerRequest request, boolean includeStackTrace) {
		Map<String, Object> errorAttributes = new LinkedHashMap<>();
		errorAttributes.put("success", false);
		Throwable error = getError(request);
		HttpStatus errorStatus = determineHttpStatus(error);
		errorAttributes.put("code", errorStatus.value());
		errorAttributes.put("message", determineMessage(error));
		return errorAttributes;
	}
	
	protected HttpStatus determineHttpStatus(Throwable error) {
		if (error instanceof ResponseStatusException) {
			return ((ResponseStatusException) error).getStatus();
		}
		ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(error.getClass(),
				ResponseStatus.class);
		if (responseStatus != null) {
			return responseStatus.code();
		}
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}

	protected String determineMessage(Throwable error) {
		if (error instanceof WebExchangeBindException) {
			return error.getMessage();
		}
		if (error instanceof ResponseStatusException) {
			return ((ResponseStatusException) error).getReason();
		}
		ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(error.getClass(),
				ResponseStatus.class);
		if (responseStatus != null) {
			return responseStatus.reason();
		}
		Throwable rootError = Throwables.getRootCause(error);
		return rootError.getMessage();
	}

	protected boolean onlyAcceptsTextHtml(ServerRequest request) {
		try {
			List<MediaType> acceptedMediaTypes = request.headers().accept();
			acceptedMediaTypes.remove(MediaType.ALL);
			MediaType.sortBySpecificityAndQuality(acceptedMediaTypes);
			return acceptedMediaTypes.stream().anyMatch(MediaType.TEXT_HTML::isCompatibleWith);
		}
		catch (InvalidMediaTypeException ex) {
			return false;
		}
	}
}