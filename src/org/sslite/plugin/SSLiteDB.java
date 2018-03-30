package org.sslite.plugin;

import org.sslite.plugin.db.SSlite;
import org.sslite.plugin.db.SSliteCreator;

import android.content.Context;

/**
 * 
 * @author Liu Song
 */
public class SSLiteDB {
	
	private static SSlite sslite;
	
	private static SSLiteDB db = null;
	
	private boolean isInit = false;
	private SSLiteDB () {
		
	}
	
	public static SSLiteDB getInstance() {
		if (db == null) {
			synchronized (SSliteCreator.class) {
				if (db == null) {
					db = new SSLiteDB();
				}
			}
		}
		return db;
	}
	
	
	public void init(Context context, SSLiteConfig config) {
		if (isInit) {
			return ;
		}
		sslite = SSliteCreator.create(context, config.getDbName(), config.getDbVersion());
		isInit = true;
	}
	
	public void init(Context context) {
		SSLiteConfig config = new SSLiteConfig();
		init(context, config);
	}
	
	public SSlite getSSlite() {
		if (!isInit) {
			throw new RuntimeException("The SDK not initialized");
		}
		return sslite;
	}
	
	
	public void release() {
		if (isInit) {
			isInit = false;
			sslite = null;
		}
	}

	
}
