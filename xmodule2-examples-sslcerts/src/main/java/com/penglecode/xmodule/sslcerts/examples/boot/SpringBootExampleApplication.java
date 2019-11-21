package com.penglecode.xmodule.sslcerts.examples.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.penglecode.xmodule.BasePackage;

@SpringBootApplication(scanBasePackageClasses=BasePackage.class)
public class SpringBootExampleApplication {

	public static void main(String[] args) {
		//System.setProperty("spring.aop.auto", "false");
		SpringApplication.run(SpringBootExampleApplication.class, args);
	}
	
}