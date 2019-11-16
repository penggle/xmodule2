package com.penglecode.xmodule.webflux.examples.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.penglecode.xmodule.BasePackage;

@SpringBootApplication(scanBasePackageClasses = BasePackage.class)
public class WebfluxExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebfluxExampleApplication.class, args);
	}

}