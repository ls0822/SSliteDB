package org.sslite.plugin.db.exception;

public class SqlStringErrorException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8499873398759926628L;
	
	public SqlStringErrorException(){}
	public SqlStringErrorException(String message){
		super(message);
	}

}
