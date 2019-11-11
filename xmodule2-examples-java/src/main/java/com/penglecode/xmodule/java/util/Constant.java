package com.penglecode.xmodule.java.util;

import org.springframework.core.ResolvableType;

@SuppressWarnings("unchecked")
public abstract class Constant<T> implements IConstant<T> {

	private static ConstantPool<Object> constantPool = new DefaultEmptyConstantPool<>();
	
	private final String name;
	
	private final Class<T> type;
	
	private final T defaultValue;

	public Constant(String name) {
		this(name, null);
	}
	
	public Constant(String name, T defaultValue) {
		super();
		this.name = name;
		this.defaultValue = defaultValue;
		this.type = (Class<T>) ResolvableType.forClass(getClass()).getSuperType().getGeneric(0).resolve();
		System.out.println(this.type);
	}
	
	@Override
	public T value() {
		return (T) constantPool.valueOf(name, (Class<Object>) type, defaultValue);
	}

	protected static void setConstantPool(ConstantPool<Object> constantPool) {
		Constant.constantPool = constantPool;
	}
	
	private static class DefaultEmptyConstantPool<T> implements ConstantPool<T> {

		@Override
		public T valueOf(String name, Class<T> type, T defaultValue) {
			return null;
		}
		
	}
	
}
