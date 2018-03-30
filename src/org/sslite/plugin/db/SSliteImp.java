package org.sslite.plugin.db;

import java.util.ArrayList;
import java.util.List;

import org.sslite.plugin.db.model.OrderBy;

import android.content.Context;
import android.os.CancellationSignal;

class SSliteImp implements SSlite {
	
	SSliteService sslite;
	
	public SSliteImp(Context context, String dbName, int dbVersion) {
		sslite = new SSliteService(context, dbName, dbVersion);
	}

	@Override
	public <T> boolean insert(T obj) {
		List<T> objs = new ArrayList<T>();
		objs.add(obj);
		return sslite.insert(objs);
	}

	@Override
	public <T> boolean batchInsert(List<T> objs) {
		return sslite.insert(objs);
	}

	@Override
	public <T> boolean delete(T obj) {
		List<T> objs = new ArrayList<T>();
		objs.add(obj);
		return sslite.delete(objs);
	}

	@Override
	public <T> boolean batchDelete(List<T> objs) {
		return sslite.delete(objs);
	}

	@Override
	public boolean deleteOrderDatas(Class<?> clazz, long count, OrderBy orderBy) {
		return sslite.deleteOrderDatas(clazz, count, orderBy);
	}

	@Override
	public <T> boolean update(T obj) {
		List<T> objs = new ArrayList<T>();
		objs.add(obj);
		return sslite.update(objs);
	}
	
	@Override
	public <T> boolean batchUpdate(List<T> objs) {
		return sslite.update(objs);
	}

	@Override
	public <T> boolean find(T obj) {
		return sslite.find(obj);
	}

	@Override
	public <T> List<T> findAll(Class<T> clazz) {
		return sslite.find(clazz, null, -1, -1);
	}

	@Override
	public <T> List<T> find(Class<T> clazz, OrderBy orderBy, int limitStart, int limitSize) {
		return sslite.find(clazz, orderBy, limitStart, limitSize);
	}

	@Override
	public <T> List<T> find(Class<T> clazz, boolean distinct, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy, String limit,
			CancellationSignal cancellationSignal) {
		return sslite.find(clazz, distinct, columns, selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal);
	}

	@Override
	public long queryDataCount(Class<?> clazz) {
		return sslite.queryDataCount(clazz);
	}
	
	
	
}
