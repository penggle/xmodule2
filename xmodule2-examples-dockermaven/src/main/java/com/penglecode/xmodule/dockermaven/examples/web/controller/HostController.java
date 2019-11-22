package com.penglecode.xmodule.dockermaven.examples.web.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/host")
public class HostController {

	@GetMapping(value="/times", produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Object getHostTimes() {
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("timeZone", TimeZone.getDefault());
		result.put("nowTime", LocalDateTime.now());
		return result;
	}
	
	@GetMapping(value="/envs", produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Object getHostEnvs() {
		return System.getenv();
	}
	
}
