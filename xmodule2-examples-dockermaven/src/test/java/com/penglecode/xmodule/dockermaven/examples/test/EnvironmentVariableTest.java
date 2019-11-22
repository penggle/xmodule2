package com.penglecode.xmodule.dockermaven.examples.test;

import java.util.Map;

public class EnvironmentVariableTest {

	public static void main(String[] args) {
		System.out.println(System.getenv("DOCKER_HOST"));
		System.out.println("----------------------------------");
		Map<String,String> envs = System.getenv();
		envs.forEach((k, v) -> {
			System.out.println(k + " = " + v);
		});
	}

}
