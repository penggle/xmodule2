package com.penglecode.xmodule.security.oauth2.examples.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.penglecode.xmodule.common.support.Result;
import com.penglecode.xmodule.common.util.JsonUtils;
import com.penglecode.xmodule.common.web.support.HttpAPIResourceSupport;
import com.penglecode.xmodule.security.oauth2.examples.model.Joke;
import com.penglecode.xmodule.security.oauth2.examples.service.JokeService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/joke")
public class JokeController extends HttpAPIResourceSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(JokeController.class);
	
	@Autowired
	private JokeService jokeService;
	
	@GetMapping(value="/list", produces=MediaType.APPLICATION_JSON_VALUE)
	public Result<List<Joke>> getJokeList(
			@AuthenticationPrincipal Jwt jwt,
			@RequestParam(name="type") String type,
			@RequestParam(name="page", defaultValue="1") Integer page,
			@RequestParam(name="count", defaultValue="10") Integer count) {
		LOGGER.info(">>> jwt = {}", JsonUtils.object2Json(jwt));
		LOGGER.info(">>> getJokeList({}, {}, {})", type, page, count);
		Flux<Joke> jokeFlux = jokeService.getJokeList(type, page, count);
		return Result.success().data(jokeFlux.collectList().block()).build();
	}
	
	@GetMapping(value="/{sid}", produces=MediaType.APPLICATION_JSON_VALUE)
	public Result<Joke> getJokeBySid(@AuthenticationPrincipal Jwt jwt, @PathVariable("sid") String sid) {
		LOGGER.info(">>> jwt = {}", JsonUtils.object2Json(jwt));
		LOGGER.info(">>> getJokeBySid({})", sid);
		Mono<Joke> jokeMono = jokeService.getJokeBySid(sid);
		return Result.success().data(jokeMono.block()).build();
	}
	
}
