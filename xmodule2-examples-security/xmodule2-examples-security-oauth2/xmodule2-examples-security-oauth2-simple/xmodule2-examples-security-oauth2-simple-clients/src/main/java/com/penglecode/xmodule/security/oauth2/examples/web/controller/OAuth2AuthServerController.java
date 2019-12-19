package com.penglecode.xmodule.security.oauth2.examples.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientScopesResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.ProtocolMappersResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.penglecode.xmodule.common.support.Result;
import com.penglecode.xmodule.common.util.ObjectUtils;
import com.penglecode.xmodule.common.util.StringUtils;
import com.penglecode.xmodule.security.oauth2.examples.config.OAuth2AuthServerConfigProperties;
import com.penglecode.xmodule.security.oauth2.examples.config.OAuth2AuthServerConfigProperties.OAuth2ClientConfigProperties;

@RestController
@RequestMapping("/api/authserver")
public class OAuth2AuthServerController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthServerController.class);
	
	@Autowired
	private Keycloak keycloak;
	
	@Autowired
	private OAuth2AuthServerConfigProperties authServerConfigProperties;
	
	@GetMapping(value="/reinit", produces=MediaType.APPLICATION_JSON_VALUE)
	public Result<Object> reinit() {
		LOGGER.info(">>> 重新初始化Keycloak服务端配置开始...");
		removeRealm();
		createRealm();
		createClentScopes();
		createClients();
		createDemoUsers();
		return Result.success().build();
	}
	
	/**
	 * 删除Realm(如果存在的话)
	 */
	protected void removeRealm() {
		List<RealmRepresentation> allRealms = keycloak.realms().findAll();
		if(!CollectionUtils.isEmpty(allRealms)) {
			allRealms.stream().filter(r -> r.getRealm().equals(authServerConfigProperties.getRealm())).findFirst().ifPresent(r -> {
				RealmResource realmResource = keycloak.realm(r.getRealm());
				realmResource.remove();
				LOGGER.info(">>> 删除Realm({})成功!", r.getRealm());
			});
		}
	}
	
	/**
	 * 创建Realm
	 */
	protected void createRealm() {
		RealmRepresentation realm = new RealmRepresentation();
		realm.setId(authServerConfigProperties.getRealm()); //如果不设ID则或自动生成UUID填充
		realm.setRealm(authServerConfigProperties.getRealm()); //realm名称应该唯一
		realm.setEnabled(true);
		realm.setDisplayName("Spring Security OAuth2 示例");
		realm.setDisplayNameHtml("<h3>Spring Security OAuth2 示例</h3>");
		realm.setAccessTokenLifespan(30 * 60); //设置access_token过期时间(秒),此设置还可以在具体的Client那里进行覆盖
		realm.setSsoSessionIdleTimeout(60 * 60 * 24); //设置refresh_token过期时间(秒)
		keycloak.realms().create(realm);
		LOGGER.info(">>> 创建Realm({})成功!", realm.getRealm());
	}
	
	/**
	 * 创建ClentScopes
	 */
	protected void createClentScopes() {
		ClientScopesResource clientScopesResource = keycloak.realm(authServerConfigProperties.getRealm()).clientScopes();
		
		for(OAuth2ClientConfigProperties clientConfig : authServerConfigProperties.getClients()) {
			List<ClientScopeRepresentation> allClientScopes = clientScopesResource.findAll();
			allClientScopes = ObjectUtils.defaultIfNull(allClientScopes, new ArrayList<>());
			boolean notExists = allClientScopes.stream().noneMatch(s -> s.getName().equals(clientConfig.getScope()));
			if(notExists) {
				ClientScopeRepresentation clientScope = new ClientScopeRepresentation();
				clientScope.setName(clientConfig.getScope());
				clientScope.setProtocol("openid-connect");
				clientScope.setDescription(clientConfig.getDescription() + "的ClientScope");
				clientScopesResource.create(clientScope);
				LOGGER.info(">>> 创建ClientScope({})成功!", clientScope.getName());
			}
		}
	}
	
	/**
	 * 创建Client
	 */
	protected void createClients() {
		ClientsResource clientsResource = keycloak.realm(authServerConfigProperties.getRealm()).clients();
		
		for(OAuth2ClientConfigProperties clientConfig : authServerConfigProperties.getClients()) {
			List<ClientRepresentation> allClients = clientsResource.findAll();
			allClients = ObjectUtils.defaultIfNull(allClients, new ArrayList<>());
			boolean notExists = allClients.stream().noneMatch(c -> c.getClientId().equals(clientConfig.getClientId()));
			if(notExists) {
				ClientRepresentation client = new ClientRepresentation();
				client.setId(UUID.randomUUID().toString());
				client.setClientId(clientConfig.getClientId());
				client.setName("${client_" + clientConfig.getClientId() + "}");
				client.setProtocol("openid-connect");
				client.setBearerOnly(false);
				client.setPublicClient(false);
				client.setEnabled(true);
				client.setClientAuthenticatorType("client-secret");
				client.setSecret(clientConfig.getClientSecret());
				client.setDescription(clientConfig.getDescription() + "的Client");
				//支持使用授权码进行标准的基于OpenID连接重定向的身份验证,例如OpenID Connect或者OAuth2授权码模式(Authorization Code)
				client.setStandardFlowEnabled(true);
				//支持直接访问授权,这意味着客户端可以访问用户的用户名/密码，并直接与Keycloak服务器交换访问令牌,例如OAuth2密码模式(Resource Owner Password Credentials)
				client.setDirectAccessGrantsEnabled(true);
				//允许您对此客户端进行身份验证，以密钥隐藏和检索专用于此客户端的访问令牌,例如OAuth2客户端模式(Client Credentials)
				client.setServiceAccountsEnabled(true);
				//为客户端启用/禁用细粒度授权支持
				ResourceServerRepresentation authorizationSettings = new ResourceServerRepresentation();
				client.setAuthorizationSettings(authorizationSettings);
				
				String baseUrl = "http://127.0.0.1:" + authServerConfigProperties.getServerPort();
				client.setBaseUrl(baseUrl);
				client.setRedirectUris(Arrays.asList(baseUrl + "/*"));
				
				Map<String,String> attributes = new HashMap<String,String>();
				//设置access_token的寿命(3600秒)
				attributes.put("access.token.lifespan", "3600");
				client.setAttributes(attributes);
				
				//设置默认的ClientScope
				client.setDefaultClientScopes(Arrays.asList(clientConfig.getScope()));
				clientsResource.create(client);
				LOGGER.info(">>> 创建Client({})成功!", client.getClientId());
				
				//配置客户端的Mappers，以达到jwt令牌瘦身的目的
				ProtocolMappersResource mapperResource = clientsResource.get(client.getId()).getProtocolMappers();
				List<ProtocolMapperRepresentation> mappers = mapperResource.getMappers();
				if(!CollectionUtils.isEmpty(mappers)) {
					mappers.forEach(m -> {
						m.getConfig().put("access.token.claim", "false");
						mapperResource.update(m.getId(), m);
						LOGGER.info(">>> 更新Client({})的Mapper({})!", client.getClientId(), m.getName());
					});
				}
			}
		}
	}
	
	/**
	 * 创建用户
	 */
	protected void createDemoUsers() {
		UsersResource usersResource = keycloak.realm(authServerConfigProperties.getRealm()).users();
		
		for(OAuth2ClientConfigProperties clientConfig : authServerConfigProperties.getClients()) {
			if(!StringUtils.isEmpty(clientConfig.getDemoUsername())) {
				List<UserRepresentation> allUsers = usersResource.list();
				allUsers = ObjectUtils.defaultIfNull(allUsers, new ArrayList<>());
				boolean notExists = allUsers.stream().noneMatch(c -> c.getUsername().equals(clientConfig.getDemoUsername()));
				if(notExists) {
					UserRepresentation user = new UserRepresentation();
					user.setUsername(clientConfig.getDemoUsername());
					CredentialRepresentation credential = new CredentialRepresentation();
					credential.setType(CredentialRepresentation.PASSWORD);
					credential.setValue(clientConfig.getDemoPassword());
					credential.setTemporary(false);
					user.setCredentials(Arrays.asList(credential));
					user.setEnabled(true);
					usersResource.create(user);
					LOGGER.info(">>> 创建User({})成功!", user.getUsername());
				}
			}
		}
	}
	
}
