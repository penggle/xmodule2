package com.penglecode.xmodule.spring5.core.annotation;

import java.util.Date;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/my/app")
public class MyAppController extends AbstractAppController {

	@GetMapping(value="/name", produces=MediaType.APPLICATION_JSON_VALUE)
	public Object getName() {
		return "myapp";
	}
	
	@RequestMapping(value="/time", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public Object getTime() {
		return new Date();
	}
	
}
