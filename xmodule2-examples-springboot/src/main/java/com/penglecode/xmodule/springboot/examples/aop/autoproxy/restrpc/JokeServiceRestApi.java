package com.penglecode.xmodule.springboot.examples.aop.autoproxy.restrpc;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestApi(clientName="myJokeServiceRestApi", clientUrl="https://api.apiopen.top", clientDesc="段子API")
public interface JokeServiceRestApi {

	@GetMapping(value="/getJoke", produces=MediaType.APPLICATION_JSON_VALUE)
	public ApiResult<List<Joke>> getJokeList(@RequestParam String type, @RequestParam Integer page, @RequestParam Integer count);
	
	@GetMapping(value="/getSingleJoke", produces=MediaType.APPLICATION_JSON_VALUE)
	public ApiResult<Joke> getJokeById(@RequestParam String sid);
	
}
