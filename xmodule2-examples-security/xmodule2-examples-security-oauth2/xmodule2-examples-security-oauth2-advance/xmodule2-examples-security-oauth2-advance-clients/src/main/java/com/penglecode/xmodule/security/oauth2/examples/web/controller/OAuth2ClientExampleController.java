package com.penglecode.xmodule.security.oauth2.examples.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;

import static com.penglecode.xmodule.common.security.oauth2.client.DefaultOAuth2AuthorizedClientExchangeFilter.authentication;
import static com.penglecode.xmodule.common.security.oauth2.client.DefaultOAuth2AuthorizedClientExchangeFilter.clientRegistrationId;

import com.penglecode.xmodule.common.support.Result;
import com.penglecode.xmodule.common.web.support.HttpAPIResourceSupport;
import com.penglecode.xmodule.security.oauth2.examples.model.Joke;

@Controller
public class OAuth2ClientExampleController extends HttpAPIResourceSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2ClientExampleController.class);
	
	@Value("${spring.security.oauth2.examples.resource-server-url}")
	private String resourceServerUrl;
	
	@Autowired
	private WebClient webClient;
	
	@GetMapping(value={"/", "/index"}, produces=MediaType.TEXT_HTML_VALUE)
	public String index() {
		LOGGER.info(">>> goto index");
		return "index";
	}
	
	/**
	 * 通过OAuth2 - client_credentials模式获取Joke列表
	 * 
	 * 调用流程：
	 * 1、WebClient过滤器调用OAuth2AuthorizedClientManager.authorize(...)方法获取OAuth2AuthorizedClient，代码见#ServletOAuth2AuthorizedClientExchangeFilterFunction.filter(ClientRequest, ExchangeFunction)
	 * 2、OAuth2AuthorizedClientManager在OAuth2AuthorizationContext上下文中未找到OAuth2AuthorizedClient，则会首次获取(后续会使用OAuth2AuthorizationContext中的缓存)，代码见#ClientCredentialsOAuth2AuthorizedClientProvider.authorize(OAuth2AuthorizationContext)
	 * 3、WebClient在请求头中添加Authorization Bearer头，代码见#ServletOAuth2AuthorizedClientExchangeFilterFunction.filter(ClientRequest, ExchangeFunction)
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/jokes", params="grant_type=client_credentials", produces=MediaType.TEXT_HTML_VALUE)
	public String getJokesByClientCredentials(Model model) {
		Map<String,Object> parameter = new HashMap<String,Object>();
		parameter.put("type", "");
		parameter.put("page", 1);
		parameter.put("count", 10);
		//通过OAuth2 - client_credentials模式获取Joke列表不需要登录,强制设置authentication为null
		List<Joke> jokeList = getJokeList(parameter, clientRegistrationId("default-client-client-credentials").andThen(authentication(null)));
		model.addAttribute("jokeList", jokeList);
		return "jokes";
	}
	
	/**
	 * 通过OAuth2 - authorization_code模式获取Joke列表
	 * 
	 * 调用流程：
	 * 1、WebClient过滤器调用OAuth2AuthorizedClientManager.authorize(...)方法获取OAuth2AuthorizedClient，代码见#ServletOAuth2AuthorizedClientExchangeFilterFunction.filter(ClientRequest, ExchangeFunction)
	 * 2、OAuth2AuthorizedClientManager调用AuthorizationCodeOAuth2AuthorizedClientProvider.authorize(...)方法，如果未授权则抛出#ClientAuthorizationRequiredException，代码见#AuthorizationCodeOAuth2AuthorizedClientProvider.authorize(OAuth2AuthorizationContext)
	 * 3、#OAuth2AuthorizationRequestRedirectFilter.doFilterInternal(...)方法的catch模块中处理了ClientAuthorizationRequiredException，会重定向到如下的authorization-uri:
	 * 
	 * http://127.0.0.1:8090/auth/realms/oauth2-simple-examples/protocol/openid-connect/auth?response_type=code&client_id=user-client&scope=user&state=rO16Y9UmQhaPj4rj9vDLg-wpCx189oFjqN46inN2zdk%3D&redirect_uri=http://127.0.0.1:18081/{action}/oauth2/code/{registrationId}
	 * 
	 * 该页面是OAuth2授权服务器提供的登录页面(例如keycloak提供的默认登录页面)
	 * 
	 * 4、上一步登录成功以后，请求会重定向到redirect_uri指定的地址(http://127.0.0.1:18081/{action}/oauth2/code/{registrationId})，该redirect_uri会被#OAuth2AuthorizationCodeGrantFilter.processAuthorizationResponse(...)方法处理
	 * (其中authenticationManager.authenticate(authenticationRequest)根据code拿token,进而创建OAuth2AuthorizedClient,最后做重定向,重定向时会优先使用savedRequest的地址,如果没有则重定向到redirect_uri)
	 * 
	 * 5、WebClient在请求头中添加Authorization Bearer头，代码见#ServletOAuth2AuthorizedClientExchangeFilterFunction.filter(ClientRequest, ExchangeFunction)
	 *
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/jokes", params="grant_type=authorization_code", produces=MediaType.TEXT_HTML_VALUE)
	public String getJokesByAuthorizationCode(Model model) {
		Map<String,Object> parameter = new HashMap<String,Object>();
		parameter.put("type", "");
		parameter.put("page", 1);
		parameter.put("count", 10);
		//通过OAuth2 - authorization_code模式获取Joke列表,需要登录
		List<Joke> jokeList = getJokeList(parameter, clientRegistrationId("default-client-authorization-code"));
		model.addAttribute("jokeList", jokeList);
		return "jokes";
	}
	
	/**
	 * 通过OAuth2 - password模式获取Joke列表
	 * 
	 * 调用流程：
	 * 1、WebClient过滤器调用OAuth2AuthorizedClientManager.authorize(...)方法获取OAuth2AuthorizedClient，代码见#ServletOAuth2AuthorizedClientExchangeFilterFunction.filter(ClientRequest, ExchangeFunction)
	 * 2、OAuth2AuthorizedClientManager在OAuth2AuthorizationContext上下文中未找到OAuth2AuthorizedClient，则会首次获取(后续会使用OAuth2AuthorizationContext中的缓存)，代码见#PasswordOAuth2AuthorizedClientProvider.authorize(OAuth2AuthorizationContext)
	 * 3、WebClient在请求头中添加Authorization Bearer头，代码见#ServletOAuth2AuthorizedClientExchangeFilterFunction.filter(ClientRequest, ExchangeFunction)
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/jokes", params="grant_type=password", produces=MediaType.TEXT_HTML_VALUE)
	public String getJokesByPassword(Model model) {
		Map<String,Object> parameter = new HashMap<String,Object>();
		parameter.put("type", "");
		parameter.put("page", 1);
		parameter.put("count", 10);
		//通过OAuth2 - authorization_code模式获取Joke列表,需要登录
		List<Joke> jokeList = getJokeList(parameter, clientRegistrationId("default-client-password").andThen(authentication(null)));
		model.addAttribute("jokeList", jokeList);
		return "jokes";
	}
	
	protected List<Joke> getJokeList(Map<String,Object> parameter, Consumer<Map<String, Object>> attributesConsumer) {
		LOGGER.info(">>> getJokeList({})", parameter);
		Result<List<Joke>> result = this.webClient
				.get()
				.uri(resourceServerUrl + "/api/joke/list?type={type}&page={page}&count={count}", parameter)
				.attributes(attributesConsumer)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<Result<List<Joke>>>() {})
				.block();
		return result.getData();
	}
	
}
