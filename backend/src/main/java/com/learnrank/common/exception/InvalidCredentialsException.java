package com.learnrank.common.exception;

public class InvalidCredentialsException  extends RuntimeException{

	public InvalidCredentialsException() {
		super("Invalid email or password");
	}
}
