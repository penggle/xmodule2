package com.penglecode.xmodule.keycloak.examples.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.penglecode.xmodule.BasePackage;

@SpringBootApplication(scanBasePackageClasses=BasePackage.class)
public class KeycloakExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeycloakExampleApplication.class, args);
	}
	
}