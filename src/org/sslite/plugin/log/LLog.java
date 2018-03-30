package org.sslite.plugin.log;

import org.sslite.plugin.ConfigCache;

import android.util.Log;

public class LLog {
	private static final String TAG = "SSlite";
	
	public static void v(String msg) {
		if (ConfigCache.isDebug()) {
			Log.v(TAG, msg);
		}
	}
	
	public static void d(String msg) {
		if (ConfigCache.isDebug() ) {
			Log.d(TAG, msg);
		}
	}
	
	public static void e(String msg) {
		if (ConfigCache.isDebug() ) {
			Log.e(TAG, msg);
		}
	}
	public static void i(String msg) {
		if (ConfigCache.isDebug() ) {
			Log.i(TAG, msg);
		}
	}
	public static void w(String msg) {
		if (ConfigCache.isDebug() ) {
			Log.w(TAG, msg);
		}
	}

}
