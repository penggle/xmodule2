package com.penglecode.xmodule.common.security.oauth2.client.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

import com.penglecode.xmodule.common.util.SpringUtils;

/**
 * OAuth2AuthorizedClient工具类
 * 
 * @author 	pengpeng
 * @date	2019年12月17日 下午5:22:43
 */
public class OAuth2AuthorizedClientUtils {

	private static OAuth2AuthorizedClientRepository authorizedClientRepository;
	
	private static OAuth2AuthorizedClientService authorizedClientService;
	
	private static final Object mutex = new Object();
	
	private static volatile boolean initialized = false;
	
	protected static void initialize() {
		if(!initialized) {
			synchronized (mutex) {
				if(!initialized) {
					authorizedClientService = SpringUtils.getBean(OAuth2AuthorizedClientService.class);
					try {
						authorizedClientRepository = SpringUtils.getBean(OAuth2AuthorizedClientRepository.class);
					} catch (Exception e) {} //ignore
					initialized = true;
				}
			}
		}
	}
	
	/**
	 * 加载OAuth2AuthorizedClient
	 * 
	 * 注意该方法仅仅获取被缓存的OAuth2AuthorizedClient,而不管其是否有效或已过期
	 * @param clientRegistrationId
	 * @param principal
	 * @param request
	 * @return
	 */
	public static OAuth2AuthorizedClient loadOAuth2AuthorizedClient(String clientRegistrationId, Authentication principal, HttpServletRequest request) {
		initialize();
		if(authorizedClientRepository != null) {
			return authorizedClientRepository.loadAuthorizedClient(clientRegistrationId, principal, request);
		} else if (authorizedClientService != null) {
			return authorizedClientService.loadAuthorizedClient(clientRegistrationId, principal.getName());
		}
		throw new IllegalStateException("No bean found in Spring context for type: " + OAuth2AuthorizedClientRepository.class);
	}
	
}
