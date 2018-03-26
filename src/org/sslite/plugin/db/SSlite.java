package org.sslite.plugin.db;

import java.util.List;

import org.sslite.plugin.db.model.OrderBy;

import android.os.CancellationSignal;

public interface SSlite {
	
	boolean insert(Object obj);
	<T> boolean batchInsert(List<T> objs) ;
	
	boolean delete(Object obj);
	boolean batchDelete(List<Object> objs);
	void deleteOrderDatas(Class<?> clazz, long count,OrderBy orderBy);
	
	boolean update(Object obj);
	
	<T> List<T> find(Class<T> clazz, String sql, String[] selectionArgs);
	
	<T> List<T> find(Class<T> clazz, boolean distinct, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy, String limit,
			CancellationSignal cancellationSignal);
	
	long queryDataCount(Class<?> clazz);
	
}
