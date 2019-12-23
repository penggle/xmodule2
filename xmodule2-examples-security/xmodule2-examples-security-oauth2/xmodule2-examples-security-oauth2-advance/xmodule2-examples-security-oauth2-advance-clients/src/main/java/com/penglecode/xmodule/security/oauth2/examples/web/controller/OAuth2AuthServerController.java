package com.penglecode.xmodule.security.oauth2.examples.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientScopesResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.ComponentsResource;
import org.keycloak.admin.client.resource.ProtocolMappersResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
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
import com.penglecode.xmodule.security.oauth2.examples.config.OAuth2AuthServerConfigProperties.UserStorageProviderConfigProperties;
import com.penglecode.xmodule.security.oauth2.examples.upms.model.UpmsUser;
import com.penglecode.xmodule.security.oauth2.examples.upms.service.UpmsUserService;

@RestController
@RequestMapping("/api/authserver")
public class OAuth2AuthServerController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthServerController.class);
	
	@Autowired
	private Keycloak keycloak;
	
	@Autowired
	private UpmsUserService upmsUserService;
	
	@Autowired
	private OAuth2AuthServerConfigProperties authServerConfigProperties;
	
	@GetMapping(value="/reinit", produces=MediaType.APPLICATION_JSON_VALUE)
	public Result<Object> reinit() {
		LOGGER.info(">>> 重新初始化Keycloak服务端配置开始...");
		removeRealm();
		createRealm();
		createClentScopes();
		createClients();
		createUpmsUsers();
		addUpmsUserStorageProvider();
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
				//设置access_token的寿命(秒)
				attributes.put("access.token.lifespan", String.valueOf(30 * 60));
				client.setAttributes(attributes);
				
				//设置默认的ClientScope
				client.setDefaultClientScopes(Arrays.asList(clientConfig.getScope()));
				
				//Authorization Code/Password模式的Client需要设置Client的Mappers，定制AccessToken中的返回字段(增加username、user_roles字段)
				if(clientConfig.getAuthorizationGrantType().equals("authorization_code") || clientConfig.getAuthorizationGrantType().equals("password")) {
					List<ProtocolMapperRepresentation> mappers = new ArrayList<ProtocolMapperRepresentation>();
					ProtocolMapperRepresentation mapper = null;
					Map<String,String> config = null;
					
					mapper = new ProtocolMapperRepresentation();
					mapper.setName("username");
					mapper.setProtocol("openid-connect");
					mapper.setProtocolMapper("oidc-usermodel-property-mapper");
					
					config = new LinkedHashMap<String,String>();
					config.put("userinfo.token.claim", "true");
					config.put("user.attribute", "username");
					config.put("id.token.claim", "true");
					config.put("access.token.claim", "true");
					config.put("claim.name", "username");
					config.put("jsonType.label", "String");
					mapper.setConfig(config);
					mappers.add(mapper);
					
					mapper = new ProtocolMapperRepresentation();
					mapper.setName("realm roles");
					mapper.setProtocol("openid-connect");
					mapper.setProtocolMapper("oidc-usermodel-realm-role-mapper");
					
					config = new LinkedHashMap<String,String>();
					config.put("user.attribute", "foo");
					config.put("access.token.claim", "true");
					config.put("claim.name", "user_roles");
					config.put("jsonType.label", "String");
					config.put("multivalued", "true");
					mapper.setConfig(config);
					mappers.add(mapper);
					
					client.setProtocolMappers(mappers);
				}
				
				clientsResource.create(client);
				LOGGER.info(">>> 创建Client({})成功!", client.getClientId());
				
				//配置客户端的[Client ID, Client Host, Client IP Address]Mappers，以达到jwt令牌瘦身的目的
				ProtocolMappersResource mapperResource = clientsResource.get(client.getId()).getProtocolMappers();
				List<ProtocolMapperRepresentation> mappers = mapperResource.getMappers();
				if(!CollectionUtils.isEmpty(mappers)) {
					mappers.forEach(m -> {
						if(m.getName().equals("Client ID") || m.getName().equals("Client Host") || m.getName().equals("Client IP Address")) {
							m.getConfig().put("access.token.claim", "false");
							mapperResource.update(m.getId(), m);
							LOGGER.info(">>> 更新Client({})的Mapper({})!", client.getClientId(), m.getName());
						}
					});
				}
			}
		}
	}
	
	/**
	 * 在User Federation中添加UserStorageProvider
	 */
	protected void addUpmsUserStorageProvider() {
		ComponentsResource componentsResource = keycloak.realm(authServerConfigProperties.getRealm()).components();
		List<ComponentRepresentation> allComponents = componentsResource.query();
		allComponents = ObjectUtils.defaultIfNull(allComponents, new ArrayList<>());
		UserStorageProviderConfigProperties userStorageProviderConfig = authServerConfigProperties.getUserStorageProvider();
		boolean notExists = allComponents.stream().noneMatch(c -> c.getName().equals(userStorageProviderConfig.getName()));
		if(notExists) {
			ComponentRepresentation userStorageProvider = new ComponentRepresentation();
			userStorageProvider.setName(userStorageProviderConfig.getName());
			userStorageProvider.setProviderId(userStorageProviderConfig.getProviderId());
			userStorageProvider.setProviderType("org.keycloak.storage.UserStorageProvider");
			MultivaluedHashMap<String,String> providerConfig = new MultivaluedHashMap<String,String>();
			providerConfig.put("cachePolicy", Arrays.asList("DEFAULT"));
			providerConfig.put("jndiDataSourceName", Arrays.asList(userStorageProviderConfig.getJndiDataSourceName()));
			providerConfig.put("priority", Arrays.asList("0"));
			providerConfig.put("enabled", Arrays.asList("true"));
			userStorageProvider.setConfig(providerConfig);
			componentsResource.add(userStorageProvider);
			LOGGER.info(">>> 添加UserStorageProvider({})!", userStorageProviderConfig.getName());
		}
	}
	
	/**
	 * 创建用户
	 */
	protected void createUpmsUsers() {
		for(OAuth2ClientConfigProperties clientConfig : authServerConfigProperties.getClients()) {
			if(!StringUtils.isEmpty(clientConfig.getDemoUsername())) {
				UpmsUser user = upmsUserService.getUserByUserName(clientConfig.getDemoUsername());
				boolean notExists = user == null;
				if(notExists) {
					user = new UpmsUser();
					user.setUserName(clientConfig.getDemoUsername());
					user.setPassword(clientConfig.getDemoPassword());
					user.setEmail(clientConfig.getDemoUsername() + "@qq.com");
					user.setRealName(clientConfig.getDemoUsername());
					user.setMobilePhone("13812345678");
					upmsUserService.createUser(user);
					LOGGER.info(">>> 创建UpmsUser({})成功!", user.getUserName());
				}
			}
		}
	}
	
}
