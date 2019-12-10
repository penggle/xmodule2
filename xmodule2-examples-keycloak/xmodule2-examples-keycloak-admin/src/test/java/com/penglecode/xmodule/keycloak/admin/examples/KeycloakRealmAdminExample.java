package com.penglecode.xmodule.keycloak.admin.examples;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.penglecode.xmodule.keycloak.examples.boot.KeycloakExampleApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.NONE, classes={KeycloakExampleApplication.class})
public class KeycloakRealmAdminExample {

	@Autowired
	private Keycloak keycloak;
	
	@Test
	public void getAllRealms() {
		keycloak.realms().findAll().stream().forEach(realm -> {
			System.out.println(realm);
		});
	}
	
	/**
	 * 创建Realm
	 */
	@Test
	public void createRealm() {
		RealmRepresentation realm = new RealmRepresentation();
		realm.setRealm("myrealm1");
		realm.setEnabled(true);
		keycloak.realms().create(realm);
	}
	
	/**
	 * 修改Realm
	 */
	@Test
	public void updateRealm() {
		RealmResource realmResource = keycloak.realm("myrealm1");
		RealmRepresentation realm = realmResource.toRepresentation();
		realm.setDisplayName("My Realm1");
		realm.setDisplayNameHtml("<h3>This is my relam1</h3>");
		realm.setEnabled(true);
		realmResource.update(realm);
	}
	
	/**
	 * 删除Realm
	 */
	@Test
	public void deleteRealm() {
		RealmResource realmResource = keycloak.realm("myrealm1");
		realmResource.remove();
	}
	
}
