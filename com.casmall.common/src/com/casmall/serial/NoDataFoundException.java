package com.casmall.serial;

public class NoDataFoundException extends Exception {
	private static final long serialVersionUID = 1L;
	public NoDataFoundException(String str){
		super(str);
	}
}
