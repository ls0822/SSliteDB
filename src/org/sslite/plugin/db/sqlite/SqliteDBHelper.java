package org.sslite.plugin.db.sqlite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by song on 2016/4/25.
 */
abstract class SqliteDBHelper extends SQLiteOpenHelper{

    public SqliteDBHelper(Context context, String dbName, int dbVersion) {
        super(context,dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	
    }
 
	@Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		upgrade(db);
    }
	
	protected abstract void upgrade(SQLiteDatabase db);

	


}
