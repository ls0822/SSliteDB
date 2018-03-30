package org.sslite.plugin.db;

import java.util.List;

import org.sslite.plugin.db.model.OrderBy;

import android.os.CancellationSignal;

interface ISSliteService {
	<T> boolean insert(List<T> objs);
	<T> boolean delete(List<T> objs);	
	
	boolean deleteOrderDatas(Class<?> clazz, long count,OrderBy orderBy);
	
	<T> boolean update(List<T> objs);
		
	<T> boolean find(T obj);
		
	<T> List<T> find(Class<T> clazz,OrderBy orderBy, int limitStart,int limitSize);
	
	<T> List<T> find(Class<T> clazz, boolean distinct, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy, String limit,
			CancellationSignal cancellationSignal);
	
	long queryDataCount(Class<?> clazz);
}
