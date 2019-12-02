package com.penglecode.xmodule.springboot.examples.web.controller;

import java.util.Collections;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.penglecode.xmodule.common.util.DateTimeUtils;

@RestController
@RequestMapping("/api/example1")
public class Example1Controller {

	@GetMapping(value="/nowtime", produces=MediaType.APPLICATION_JSON_VALUE)
	public Object getNowTime() {
		return Collections.singletonMap("nowTime", DateTimeUtils.formatNow());
	}
	
}
