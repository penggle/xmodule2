package com.penglecode.xmodule.security.oauth2.examples.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.penglecode.xmodule.security.oauth2.examples.model.Joke;
import com.penglecode.xmodule.security.oauth2.examples.support.OpenApiResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 笑话服务
 * 
 * @author 	pengpeng
 * @date	2019年11月13日 上午10:54:21
 */
@Service("jokeService")
public class JokeService {

	private final WebClient webClient;
	
	public JokeService(WebClient.Builder builder) {
		this.webClient = builder.baseUrl("https://api.apiopen.top").build();
	}
	
	public Flux<Joke> getJokeList(String type, int page, int count) {
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("type", type);
		parameter.put("page", page);
		parameter.put("count", count);
		return webClient.get().uri("/getJoke?page={page}&count={count}&type={type}", parameter).retrieve()
				.bodyToMono(new ParameterizedTypeReference<OpenApiResult<List<Joke>>>() {}).flatMapMany(result -> {
					return Flux.fromIterable(result.getResult());
				});
	}
	
	public Mono<Joke> getJokeBySid(String sid) {
		return webClient.get().uri("/getSingleJoke?sid={sid}", Collections.singletonMap("sid", sid)).retrieve()
				.bodyToMono(new ParameterizedTypeReference<OpenApiResult<Joke>>() {}).map(result -> result.getResult());
	}
	
}
