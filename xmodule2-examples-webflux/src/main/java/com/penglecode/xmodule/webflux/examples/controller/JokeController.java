package com.penglecode.xmodule.webflux.examples.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.penglecode.xmodule.webflux.examples.service.JokeService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/joke")
public class JokeController {

	@Autowired
	private JokeService jokeService;
	
	@GetMapping(value="/list", produces=MediaType.APPLICATION_JSON_VALUE)
	public Mono<Map<String,Object>> getJokeList(@RequestParam(name="type", defaultValue="") String type,
			@RequestParam(name="page", defaultValue="1") Integer page,
			@RequestParam(name="count", defaultValue="10") Integer count) {
		return jokeService.getJokeList(type, page, count).collectList().map(dataList -> {
			Map<String,Object> result = new HashMap<String,Object>();
			result.put("success", true);
			result.put("code", 200);
			result.put("message", "OK");
			result.put("data", dataList);
			return result;
		});
	}
	
	@GetMapping(value="/{sid}", produces=MediaType.APPLICATION_JSON_VALUE)
	public Mono<Map<String,Object>> getJokeBySid(@PathVariable("sid") String sid) {
		return jokeService.getJokeBySid(sid).map(data -> {
			Map<String,Object> result = new HashMap<String,Object>();
			result.put("success", true);
			result.put("code", 200);
			result.put("message", "OK");
			result.put("data", data);
			return result;
		});
	}
	
}
