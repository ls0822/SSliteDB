package org.sslite.plugin.db;

import java.util.List;

import org.sslite.plugin.log.LLog;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.CancellationSignal;
import android.util.Log;

class SqliteDao extends SQLiteOpenHelper {

	public interface OnDataBaseChangeListener {

		void onCreate(SQLiteDatabase db);

		void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

	}

	public interface OnPackageDataCallback<T> {
		public List<T> onPackage(Class<T> clazz, Cursor cursor);
	}

	private OnDataBaseChangeListener l;

	public SqliteDao(Context context, String name, int version, OnDataBaseChangeListener l) {
		super(context, name, null, version);
		this.l = l;

	}

	public SqliteDao(Context context, String name, CursorFactory factory, int version,
			DatabaseErrorHandler errorHandler, OnDataBaseChangeListener l) {
		super(context, name, factory, version, errorHandler);
		this.l = l;

	}

	public SqliteDao(Context context, String name, CursorFactory factory, int version, OnDataBaseChangeListener l) {
		super(context, name, factory, version);
		this.l = l;

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (l != null) {
			l.onCreate(db);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (l != null) {
			l.onUpgrade(db, oldVersion, newVersion);
		}
	}

	protected boolean create(List<String> sqls) {
		long time1 = System.currentTimeMillis();
		SQLiteDatabase db = getWritableDatabase();
		long time2 = System.currentTimeMillis();
		LLog.d("createDatabase time ==>" + (time2-time1));
		return execute(db, sqls);
	}

	protected long insert(List<String> sqls) {
		long time1 = System.currentTimeMillis();
		SQLiteDatabase db = getWritableDatabase();
		long time2 = System.currentTimeMillis();
		LLog.d("getWritableDatabase time ==>" + (time2-time1));
		return executeInsert(db, sqls);
	}

	protected int delete(List<String> sqls) {
		SQLiteDatabase db = getWritableDatabase();
		return executeUpdateDelete(db, sqls);
	}

	protected long update(List<String> sqls) {
		SQLiteDatabase db = getWritableDatabase();
		return executeUpdateDelete(db, sqls);
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

	protected <T> List<T> find(Class<T> clazz, String tableName, boolean distinct, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy, String limit,
			CancellationSignal cancellationSignal, OnPackageDataCallback<T> callback) {
		SQLiteDatabase db = getReadableDatabase();
		List<T> list = null;
		Cursor cursor = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			cursor = db.query(distinct, tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit,
					cancellationSignal);
		} else {
			cursor = db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		}
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

	protected long queryCount(String sql) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		db.close();
		return count;
	}

	private long executeInsert(SQLiteDatabase db, List<String> sqls) {
		LLog.d("executeInsert == start =>");
		long time = System.currentTimeMillis();
		long count = -1;
		try {
			db.beginTransaction();
			for (String sql : sqls) {
				SQLiteStatement stat = db.compileStatement(sql);
				count = stat.executeInsert();
			}
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			db.close();
		}
		LLog.d("executeInsert == end =>" + count + "/" + (System.currentTimeMillis() - time));
		return count;
	}

	private int executeUpdateDelete(SQLiteDatabase db, List<String> sqls) {
		LLog.d("executeUpdateDelete == start =>");
		long time = System.currentTimeMillis();
		int count = -1;
		try {
			db.beginTransaction();
			for (String sql : sqls) {
				SQLiteStatement stat = db.compileStatement(sql);
				count = stat.executeUpdateDelete();
			}
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			db.close();
		}
		LLog.d("executeUpdateDelete == end =>" + count + "/" + (System.currentTimeMillis() - time));
		return count;
	}

	private boolean execute(SQLiteDatabase db, List<String> sqls) {
		LLog.d("execute == start =>");
		long time = System.currentTimeMillis();
		boolean isSucc = true;
		try {
			db.beginTransaction();
			for (String sql : sqls) {
				SQLiteStatement state = db.compileStatement(sql);
				state.execute();
				db.setTransactionSuccessful();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			isSucc = false;
		} finally {
			db.endTransaction();
			db.close();
		}
		LLog.d("execute == end =>" + (System.currentTimeMillis() - time));
		return isSucc;
	}

}
