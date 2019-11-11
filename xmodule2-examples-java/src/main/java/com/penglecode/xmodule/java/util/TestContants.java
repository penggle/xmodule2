package com.penglecode.xmodule.java.util;

public class TestContants {

	public static final Constant<String> OS_NAME = new Constant<String>("os.name") {};
	
	public static void main(String[] args) {
		System.out.println(OS_NAME.value());
	}
	
}
