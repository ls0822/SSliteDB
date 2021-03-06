package org.sslite.plugin.db;

import java.util.List;

import org.sslite.plugin.db.model.OrderBy;

import android.os.CancellationSignal;

public interface SSlite {
	
	<T> boolean insert(T obj);
	<T> boolean batchInsert(List<T> objs) ;
	
	<T> boolean delete(T obj);
	
	<T> boolean batchDelete(List<T> objs);
	boolean deleteOrderDatas(Class<?> clazz, long count,OrderBy orderBy);
	
	<T> boolean update(T obj);
	
	<T> boolean batchUpdate(List<T> objs);
		
	<T> boolean find(T obj);
	
	<T> List<T> findAll(Class<T> clazz);
	
	<T> List<T> find(Class<T> clazz,OrderBy orderBy, int limitStart,int limitSize);
	
	<T> List<T> find(Class<T> clazz, boolean distinct, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy, String limit,
			CancellationSignal cancellationSignal);
	
	long queryDataCount(Class<?> clazz);
	
}
