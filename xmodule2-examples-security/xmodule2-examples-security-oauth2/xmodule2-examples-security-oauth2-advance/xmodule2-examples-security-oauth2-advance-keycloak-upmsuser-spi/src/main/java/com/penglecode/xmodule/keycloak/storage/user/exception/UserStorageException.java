package com.penglecode.xmodule.keycloak.storage.user.exception;

public class UserStorageException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UserStorageException() {
		super();
	}

	public UserStorageException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UserStorageException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserStorageException(String message) {
		super(message);
	}

	public UserStorageException(Throwable cause) {
		super(cause);
	}

}
