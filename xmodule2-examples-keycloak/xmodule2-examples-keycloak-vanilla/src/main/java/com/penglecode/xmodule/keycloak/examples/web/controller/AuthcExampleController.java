package com.penglecode.xmodule.keycloak.examples.web.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthcExampleController {

	/**
	 * 去首页
	 * @return
	 */
	@GetMapping(value="/index", produces=MediaType.TEXT_HTML_VALUE)
	public String index() {
		return "index";
	}
	
	/**
	 * 操作被禁止
	 * @return
	 */
	@GetMapping(value="/403", produces=MediaType.TEXT_HTML_VALUE)
	public String forbidden() {
		return "403";
	}
	
}
