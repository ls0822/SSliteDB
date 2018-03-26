package org.sslite.plugin.db;

import java.util.ArrayList;
import java.util.List;

import org.sslite.plugin.db.exception.NotFindTableInfoException;
import org.sslite.plugin.db.model.DBValues;
import org.sslite.plugin.db.model.OrderBy;
import org.sslite.plugin.db.model.TableInfo;
import org.sslite.plugin.db.parse.DBInfoManager;
import org.sslite.plugin.db.sql.SQLCreator;
import org.sslite.plugin.db.sqlite.SqliteDao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.CancellationSignal;

class SSliteImp extends SqliteDao implements SSlite {	
	
	private DBInfoManager dbInfo = new DBInfoManager();
	private SQLCreator sqlCreator = new SQLCreator();
	
	public SSliteImp(Context context, String dbName, int dbVersion) {
		super(context, dbName, dbVersion);
	}
	
	@Override
	protected void upgrade(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}
	
	private boolean checkCreateTable(TableInfo tableInfo) {
		if (dbInfo.getTablesInfo() == null) {
			return false;
		}
		return create(sqlCreator.createTables(dbInfo.getTablesInfo()));
	}
	

	@Override
	public boolean insert(Object obj) {
		TableInfo tableInfo = dbInfo.getTableInfo(obj);
		if (tableInfo == null) {
			throw new NotFindTableInfoException("table is null, please check your"+obj.getClass().getName());
		}
		if (checkCreateTable(tableInfo)) {
			throw new RuntimeException("table is null, please check your"+obj.getClass().getName());
		}
		
		ArrayList objs = new ArrayList();
		objs.add(obj);
		DBValues dbValues = dbInfo.getBatchContentValues(tableInfo, objs);
		List<String> sqls = sqlCreator.insert(tableInfo, dbValues);
		
		return insert(sqls);
	}

	@Override
	public <T> boolean batchInsert(List<T> objs) {
		TableInfo tableInfo = dbInfo.getTableInfo(objs.get(0));
		if (tableInfo == null) {
			throw new NotFindTableInfoException("table is null, please check your" + objs.get(0).getClass().getName());
		}
		if (checkCreateTable(tableInfo)) {
			throw new RuntimeException("table is null, please check your " + objs.get(0).getClass().getName());
		}
		
		DBValues dbValues = dbInfo.getBatchContentValues(tableInfo, objs);
		List<String> sqls = sqlCreator.insert(tableInfo, dbValues);
		
		return insert(sqls);
	}

	@Override
	public boolean delete(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean batchDelete(List<Object> objs) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void deleteOrderDatas(Class<?> clazz, long count, OrderBy orderBy) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean update(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> List<T> find(Class<T> clazz, String sql, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> find(Class<T> clazz, boolean distinct, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy, String limit,
			CancellationSignal cancellationSignal) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long queryDataCount(Class<?> clazz) {
		// TODO Auto-generated method stub
		return 0;
	}

	

}
