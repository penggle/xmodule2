package com.penglecode.xmodule.keycloak.admin.examples;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.penglecode.xmodule.common.util.CollectionUtils;
import com.penglecode.xmodule.common.util.JsonUtils;
import com.penglecode.xmodule.keycloak.examples.boot.KeycloakExampleApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.NONE, classes={KeycloakExampleApplication.class})
public class KeycloakRealmAdminExample {

	@Autowired
	private Keycloak keycloak;
	
	@Test
	public void getAllRealms() {
		keycloak.realms().findAll().stream().forEach(realm -> {
			System.out.println(JsonUtils.object2Json(realm));
		});
	}
	
	/**
	 * 创建Realm
	 */
	@Test
	public void createRealm() {
		RealmRepresentation realm = new RealmRepresentation();
		realm.setId("keycloak-examples"); //如果不设ID则或自动生成UUID填充
		realm.setRealm("keycloak-examples"); //realm名称应该唯一
		realm.setEnabled(true);
		keycloak.realms().create(realm);
	}
	
	/**
	 * 修改Realm
	 */
	@Test
	public void updateRealm() {
		RealmResource realmResource = keycloak.realm("keycloak-examples");
		RealmRepresentation realm = realmResource.toRepresentation();
		realm.setDisplayName("Keycloak Examples");
		realm.setDisplayNameHtml("<h3>This is keycloak examples!</h3>");
		realm.setEnabled(true);
		realmResource.update(realm);
	}
	
	/**
	 * 删除Realm
	 */
	@Test
	public void deleteRealm() {
		RealmResource realmResource = keycloak.realm("Keycloak-examples-vanilla");
		realmResource.remove();
	}
	
	/**
	 * 获取某个Realm下的所有客户端
	 */
	@Test
	public void getClientsByRealm() {
		List<ClientRepresentation> clients = keycloak.realm("keycloak-examples").clients().findAll();
		if(!CollectionUtils.isEmpty(clients)) {
			clients.stream().forEach(client -> {
				System.out.println(JsonUtils.object2Json(client));
			});
		}
	}
	
	/**
	 * 创建客户端
	 */
	@Test
	public void createClient() {
		ClientsResource clientsResource = keycloak.realm("keycloak-examples").clients();
		ClientRepresentation client = new ClientRepresentation();
		client.setClientId("myapp1");
		Response response = clientsResource.create(client);
		System.out.println(JsonUtils.object2Json(response));
	}
	
	/**
	 * 根据clientId获取客户端
	 */
	@Test
	public void getClientById() {
		ClientsResource clientsResource = keycloak.realm("oauth2-simple-examples").clients();
		List<ClientRepresentation> clients = clientsResource.findByClientId("app-client");
		if(!CollectionUtils.isEmpty(clients)) {
			clients.stream().forEach(client -> {
				System.out.println(JsonUtils.object2Json(client));
			});
		}
	}
	
	/**
	 * 根据clientId获取客户端
	 */
	@Test
	public void getClientMapperById() {
		//keycloak.realm("oauth2-simple-examples").clients().get("23cecf40-4ea3-4da0-8782-4b431e5c579d").getProtocolMappers().delete("33f67b1b-551e-4982-99b6-52b8e51d0cd1");
	}
	
	/**
	 * 更新客户端
	 */
	@Test
	public void updateClient() {
		ClientsResource clientsResource = keycloak.realm("keycloak-examples").clients();
		List<ClientRepresentation> clients = clientsResource.findByClientId("myapp1");
		if(!CollectionUtils.isEmpty(clients)) {
			ClientRepresentation client = clients.get(0);
			String id = client.getId();
			
			client.setName("${client_myapp1}");
			client.setDescription("My app1 client");
			//支持使用授权码进行标准的基于OpenID连接重定向的身份验证,例如OpenID Connect或者OAuth2授权码模式(Authorization Code)
			client.setStandardFlowEnabled(true);
			//支持直接访问授权,这意味着客户端可以访问用户的用户名/密码，并直接与Keycloak服务器交换访问令牌,例如OAuth2密码模式(Resource Owner Password Credentials)
			client.setDirectAccessGrantsEnabled(true);
			//允许您对此客户端进行身份验证，以密钥隐藏和检索专用于此客户端的访问令牌,例如OAuth2客户端模式(Client Credentials)
			client.setServiceAccountsEnabled(true);
			//为客户端启用/禁用细粒度授权支持
			ResourceServerRepresentation authorizationSettings = new ResourceServerRepresentation();
			client.setAuthorizationSettings(authorizationSettings);
			
			client.setRootUrl("http://127.0.0.1:8080/myapp1");
			client.setRedirectUris(Arrays.asList("http://127.0.0.1:8080/myapp1/*"));
			
			Map<String,String> attributes = new HashMap<String,String>();
			//设置access_token的寿命(1800秒)
			attributes.put("access.token.lifespan", "1800");
			client.setAttributes(attributes);
			
			clientsResource.get(id).update(client);
		};
	}
	
	/**
	 * 删除客户端
	 */
	@Test
	public void deleteClient() {
		ClientsResource clientsResource = keycloak.realm("keycloak-examples").clients();
		List<ClientRepresentation> clients = clientsResource.findByClientId("myapp1");
		if(!CollectionUtils.isEmpty(clients)) {
			String id = clients.get(0).getId();
			clientsResource.get(id).remove(); //删除
		};
	}
	
}
