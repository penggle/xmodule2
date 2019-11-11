package com.penglecode.xmodule.java.util;

public interface ConstantPool<T> {

	public T valueOf(String name, Class<T> constType, T defaultValue);
	
}