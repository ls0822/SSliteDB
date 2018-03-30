package org.sslite.plugin.db;

import java.util.List;

import org.sslite.plugin.db.SqliteDao.OnDataBaseChangeListener;
import org.sslite.plugin.db.SqliteDao.OnPackageDataCallback;
import org.sslite.plugin.db.exception.NotFindTableInfoException;
import org.sslite.plugin.db.model.DBValues;
import org.sslite.plugin.db.model.OrderBy;
import org.sslite.plugin.db.model.TableInfo;
import org.sslite.plugin.db.parse.DBInfoManager;
import org.sslite.plugin.log.LLog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.CancellationSignal;

class SSliteService implements ISSliteService {

	private DBInfoManager dbInfo ;
	private SQLCreator sqlCreator ;
	private SqliteDao sqliteDao;

	public SSliteService(Context context, String dbName, int dbVersion) {
		dbInfo = new DBInfoManager();
		sqlCreator = new SQLCreator();
		sqliteDao = new SqliteDao(context, dbName, dbVersion, new OnDataBaseChangeListener() {
			
			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				
			}
			
			@Override
			public void onCreate(SQLiteDatabase db) {

			}
		});
	}

	private boolean checkCreateTable() {
		if (dbInfo.getTablesInfo() == null) {
			return false;
		}
		return sqliteDao.create(sqlCreator.createTables(dbInfo.getTablesInfo()));
	}

	@Override
	public <T> boolean insert(List<T> objs) {
		long time1 = System.currentTimeMillis();
		TableInfo tableInfo = getCreateAndGetTable(objs.get(0).getClass());
		long time2 = System.currentTimeMillis();
		LLog.d("getCreateAndGetTable time ==>" + (time2-time1));
		DBValues dbValues = dbInfo.getBatchContentValues(tableInfo, objs);
		long time3 = System.currentTimeMillis();
		LLog.d( "getBatchContentValues time ==>" + (time3-time2));
		List<String> sqls = sqlCreator.insert(tableInfo, dbValues);
		long time4 = System.currentTimeMillis();
		LLog.d("sqlCreator time ==>" + (time4-time3));
		return sqliteDao.insert(sqls) > 0;
	}

	@Override
	public <T> boolean delete(List<T> objs) {
		TableInfo tableInfo = getCreateAndGetTable(objs.get(0).getClass());
		DBValues dbValues = dbInfo.getBatchContentValues(tableInfo, objs);
		List<String> sqls = sqlCreator.delete(tableInfo, dbValues);
		return sqliteDao.delete(sqls) > 0;
	}

	@Override
	public boolean deleteOrderDatas(Class<?> clazz, long count, OrderBy orderBy) {
		TableInfo tableInfo = getCreateAndGetTable(clazz);
		List<String> sqls = sqlCreator.deleteOrderDatas(tableInfo, count, orderBy);
		return sqliteDao.delete(sqls) > 0;
	}

	@Override
	public <T> boolean update(List<T> objs) {
		TableInfo tableInfo = getCreateAndGetTable(objs.get(0).getClass());
		DBValues dbValues = dbInfo.getBatchContentValues(tableInfo, objs);
		List<String> sqls = sqlCreator.update(tableInfo, dbValues);
		return sqliteDao.update(sqls) > 0;
	}

	@Override
	public <T> boolean find(T obj) {
		Class<T> clazz = (Class<T>) obj.getClass();
		final TableInfo tableInfo = getCreateAndGetTable(obj.getClass());
		ContentValues contentValues = dbInfo.getContentValues(tableInfo, obj);
		String sql = sqlCreator.findObjectById(tableInfo, contentValues);
		List<T> find = sqliteDao.find(clazz, sql, null, new OnPackageDataCallback<T>() {

			@Override
			public List<T> onPackage(Class<T> clazz, Cursor cursor) {
				try {
					return dbInfo.packageData(clazz, tableInfo, cursor);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
		return find == null || find.size() <=0 ? false : true;
	}

	@Override
	public <T> List<T> find(Class<T> clazz, OrderBy orderBy, int limitStart, int limitSize) {
		final TableInfo tableInfo = getCreateAndGetTable(clazz);
		String sql = sqlCreator.find(tableInfo, orderBy, limitStart, limitSize);

		return sqliteDao.find(clazz, sql, null, new OnPackageDataCallback<T>() {

			@Override
			public List<T> onPackage(Class<T> clazz, Cursor cursor) {
				try {
					return dbInfo.packageData(clazz, tableInfo, cursor);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}
	
	@Override
	public <T> List<T> find(Class<T> clazz, boolean distinct, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy, String limit,
			CancellationSignal cancellationSignal) {
		final TableInfo tableInfo = getCreateAndGetTable(clazz);
		return sqliteDao.find(clazz, tableInfo.tableName, distinct, columns, selection, selectionArgs, groupBy, having, orderBy,
				limit, cancellationSignal, new OnPackageDataCallback<T>() {
					public List<T> onPackage(Class<T> clazz, Cursor cursor) {
						try {
							return dbInfo.packageData(clazz, tableInfo, cursor);
						} catch (InstantiationException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
						return null;
					}
				});
	}

	@Override
	public long queryDataCount(Class<?> clazz) {
		TableInfo tableInfo = getCreateAndGetTable(clazz);
		String sql = sqlCreator.queryDataCount(tableInfo);
		return sqliteDao.queryCount(sql);
	}
	
	
	private TableInfo getCreateAndGetTable(Class<?> clazz) {
		TableInfo tableInfo = dbInfo.getTableInfo(clazz);
		if (tableInfo == null) {
			throw new NotFindTableInfoException("table is null, please check your" + clazz.getName());
		}
		if (!checkCreateTable()) {
			throw new RuntimeException("table is null, please check your " + clazz.getName());
		}
		return tableInfo;
	}


}
