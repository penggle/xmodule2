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
		LOGGER.info(">>> ???????????????Keycloak?????????????????????...");
		removeRealm();
		createRealm();
		createClentScopes();
		createClients();
		createUpmsUsers();
		addUpmsUserStorageProvider();
		return Result.success().build();
	}
	
	/**
	 * ??????Realm(??????????????????)
	 */
	protected void removeRealm() {
		List<RealmRepresentation> allRealms = keycloak.realms().findAll();
		if(!CollectionUtils.isEmpty(allRealms)) {
			allRealms.stream().filter(r -> r.getRealm().equals(authServerConfigProperties.getRealm())).findFirst().ifPresent(r -> {
				RealmResource realmResource = keycloak.realm(r.getRealm());
				realmResource.remove();
				LOGGER.info(">>> ??????Realm({})??????!", r.getRealm());
			});
		}
	}
	
	/**
	 * ??????Realm
	 */
	protected void createRealm() {
		RealmRepresentation realm = new RealmRepresentation();
		realm.setId(authServerConfigProperties.getRealm()); //????????????ID??????????????????UUID??????
		realm.setRealm(authServerConfigProperties.getRealm()); //realm??????????????????
		realm.setEnabled(true);
		realm.setDisplayName("Spring Security OAuth2 ??????");
		realm.setDisplayNameHtml("<h3>Spring Security OAuth2 ??????</h3>");
		realm.setAccessTokenLifespan(30 * 60); //??????access_token????????????(???),??????????????????????????????Client??????????????????
		realm.setSsoSessionIdleTimeout(60 * 60 * 24); //??????refresh_token????????????(???)
		keycloak.realms().create(realm);
		LOGGER.info(">>> ??????Realm({})??????!", realm.getRealm());
	}
	
	/**
	 * ??????ClentScopes
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
				clientScope.setDescription(clientConfig.getDescription() + "???ClientScope");
				clientScopesResource.create(clientScope);
				LOGGER.info(">>> ??????ClientScope({})??????!", clientScope.getName());
			}
		}
	}
	
	/**
	 * ??????Client
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
				client.setDescription(clientConfig.getDescription() + "???Client");
				//??????????????????????????????????????????OpenID??????????????????????????????,??????OpenID Connect??????OAuth2???????????????(Authorization Code)
				client.setStandardFlowEnabled(true);
				//????????????????????????,???????????????????????????????????????????????????/?????????????????????Keycloak???????????????????????????,??????OAuth2????????????(Resource Owner Password Credentials)
				client.setDirectAccessGrantsEnabled(true);
				//?????????????????????????????????????????????????????????????????????????????????????????????????????????,??????OAuth2???????????????(Client Credentials)
				client.setServiceAccountsEnabled(true);
				//??????????????????/???????????????????????????
				ResourceServerRepresentation authorizationSettings = new ResourceServerRepresentation();
				client.setAuthorizationSettings(authorizationSettings);
				
				String baseUrl = "http://127.0.0.1:" + authServerConfigProperties.getServerPort();
				client.setBaseUrl(baseUrl);
				client.setRedirectUris(Arrays.asList(baseUrl + "/*"));
				
				Map<String,String> attributes = new HashMap<String,String>();
				//??????access_token?????????(???)
				attributes.put("access.token.lifespan", String.valueOf(30 * 60));
				client.setAttributes(attributes);
				
				//???????????????ClientScope
				client.setDefaultClientScopes(Arrays.asList(clientConfig.getScope()));
				
				//Authorization Code/Password?????????Client????????????Client???Mappers?????????AccessToken??????????????????(??????username???user_roles??????)
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
				LOGGER.info(">>> ??????Client({})??????!", client.getClientId());
				
				//??????????????????[Client ID, Client Host, Client IP Address]Mappers????????????jwt?????????????????????
				ProtocolMappersResource mapperResource = clientsResource.get(client.getId()).getProtocolMappers();
				List<ProtocolMapperRepresentation> mappers = mapperResource.getMappers();
				if(!CollectionUtils.isEmpty(mappers)) {
					mappers.forEach(m -> {
						if(m.getName().equals("Client ID") || m.getName().equals("Client Host") || m.getName().equals("Client IP Address")) {
							m.getConfig().put("access.token.claim", "false");
							mapperResource.update(m.getId(), m);
							LOGGER.info(">>> ??????Client({})???Mapper({})!", client.getClientId(), m.getName());
						}
					});
				}
			}
		}
	}
	
	/**
	 * ???User Federation?????????UserStorageProvider
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
			LOGGER.info(">>> ??????UserStorageProvider({})!", userStorageProviderConfig.getName());
		}
	}
	
	/**
	 * ????????????
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
					LOGGER.info(">>> ??????UpmsUser({})??????!", user.getUserName());
				}
			}
		}
	}
	
}
