package org.sslite.plugin;

public class SSLiteConfig {
	
	public SSLiteConfig() {
		// TODO Auto-generated constructor stub
	}
	
	private String dbName = "sslite.db";
	private int dbVersion = 1;
	private boolean isDebug = false;
	/**
	 * @return the dbName
	 */
	public String getDbName() {
		return dbName;
	}
	/**
	 * @param dbName the dbName to set
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	/**
	 * @return the dbVersion
	 */
	public int getDbVersion() {
		return dbVersion;
	}
	/**
	 * @param dbVersion the dbVersion to set
	 */
	public void setDbVersion(int dbVersion) {
		this.dbVersion = dbVersion;
	}
	
	
	public boolean isDebug() {
		return isDebug;
	}
	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}
	
	
	
	
	
}
