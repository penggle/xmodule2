package com.penglecode.xmodule.security.oauth2.examples.web.controller;

import java.time.Instant;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.penglecode.xmodule.common.support.Result;
import com.penglecode.xmodule.common.web.support.HttpAPIResourceSupport;

@RestController
@RequestMapping("/api/server")
public class OAuth2ResourceServerController extends HttpAPIResourceSupport {

	@GetMapping(value="/nowtime", produces=MediaType.APPLICATION_JSON_VALUE)
	public Result<String> getServerNowTime() {
		return Result.success().message("OK").data(Instant.now().toString()).build();
	}
	
}