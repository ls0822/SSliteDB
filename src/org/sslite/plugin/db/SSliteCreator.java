package org.sslite.plugin.db;

import android.content.Context;

public class SSliteCreator {
	
	public static SSlite create(Context context, String dbName, int dbVersion) {
		return new SSliteImp(context, dbName, dbVersion);
	}
}
