package com.penglecode.xmodule.spring5.core.annotation;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

public class AbstractAppController {

	@GetMapping(value="/id", produces=MediaType.APPLICATION_JSON_VALUE)
	public Object getAppId() {
		return null;
	}
	
}
