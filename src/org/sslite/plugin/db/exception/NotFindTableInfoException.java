package org.sslite.plugin.db.exception;

public class NotFindTableInfoException extends RuntimeException{
	private static final long serialVersionUID = -5444928509132120915L;
	public NotFindTableInfoException(){
		
	}
	public NotFindTableInfoException(String message){
		super(message);
	}
}
