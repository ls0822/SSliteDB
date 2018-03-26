package org.sslite.plugin.db.sqlite;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public abstract class SqliteDao extends SqliteDBHelper{
		
	public interface OnPackageDataCallback <T>{
		public List<T> onPackage(Class<T> clazz, Cursor cursor);
	}
	
	public SqliteDao(Context context, String dbName, int dbVersion) {
		super(context, dbName, dbVersion);
		// TODO Auto-generated constructor stub
	}

	protected boolean create(List<String> sqls) {
		SQLiteDatabase db = getWritableDatabase();
		return execute(db, sqls);
	}

	protected boolean insert(List<String> sqls) {
		SQLiteDatabase db = getWritableDatabase();
		return execute(db, sqls);
	}
	
	protected boolean delete(List<String> sqls) {
		SQLiteDatabase db = getWritableDatabase();
		return execute(db, sqls);
	}
	
	protected boolean update(List<String> sqls) {
		SQLiteDatabase db = getWritableDatabase();
		return execute(db, sqls);
	}
	
	protected <T> List<T> find(Class<T> clazz, String sql, String[] selectionArgs, OnPackageDataCallback<T> callback) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		List<T> list = null;
		try {
			list = callback.onPackage(clazz, cursor);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
			db.close();
		}
		return list;
	}

	
	protected long queryCount(Class<?> clazz, String sql) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		db.close();
		return count;
	}
	
	

	private boolean execute(SQLiteDatabase db, List<String> sqls){
		boolean isSucc = true;
		try {
			for (String sql : sqls) {
				db.execSQL(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			isSucc = false;
		} finally {
			db.close();
		}
		return isSucc;
	}
}
