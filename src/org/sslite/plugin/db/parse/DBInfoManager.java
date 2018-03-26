package org.sslite.plugin.db.parse;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sslite.plugin.db.model.ColumnInfo;
import org.sslite.plugin.db.model.DBValues;
import org.sslite.plugin.db.model.TableInfo;

import android.content.ContentValues;

/**
 * 主要用于数据的封住
 * @author song
 *
 */
public class DBInfoManager {
	
	public HashMap<String,TableInfo> tablesInfo = new HashMap<String,TableInfo>();

	
	/**
	 * 得到所有的table信息
	 * @return
	 */
	public synchronized List<TableInfo> getTablesInfo() {
		if (tablesInfo != null && tablesInfo.size() >0) {
			List<TableInfo> infos = new ArrayList<TableInfo>();
			for (String key : tablesInfo.keySet()) {
				infos.add(tablesInfo.get(key));
			}
			return infos;
		}
		return null;
	}
	

	/**
	 * 根据对象得到当前的table的信息
	 * @param obj
	 */
	public synchronized TableInfo getTableInfo(Object obj) {
		if (tablesInfo == null || tablesInfo.size() <=0) {
			return null;
		}
		
		Class<?> clazz = ReflectUtils.getClazzByObj(obj);
		String name = clazz.getName();
		if (tablesInfo.containsKey(name)) {
			return tablesInfo.get(name);
		}
		
		TableInfo tableInfo = initTable(clazz);
		if (tableInfo == null) {
			return null;
		}
		
		tablesInfo.put(name, tableInfo);
		return tableInfo;
	}
	
	
	
	/**
	 * 根据传入对象初始化当前的对象的
	 * @param clazz
	 */
	private TableInfo initTable(Class<?> clazz) {
		TableInfo initTableInfo = AnnoParse.initTableInfo(clazz);
		return initTableInfo;		
	}
	
	
	
	/**
	 * 通过对象得到对应的内容数据，并把这些数据分装到TableInfo中
	 * @param tableinfo 表信息
	 * @param obj 对象
	 */
	public <T> DBValues getBatchContentValues(TableInfo tableinfo, List<T> objs) {
		DBValues valueInfos = null;
		for (Object obj : objs) {
			ContentValues values = null;
			Class<? extends Object> clazz = obj.getClass();
			Field[] fields = ReflectUtils.getFields(clazz);
			valueInfos = new DBValues();
			for (Field field : fields) {
				field.setAccessible(true);
				try {
					if (values == null) {
						values = new ContentValues();
					}
					field.setAccessible(true);
					ColumnInfo column = tableinfo.getColumn(field.getName());
					if (column == null) {
						continue;
					}
					Object object = field.get(obj);
					valueInfos.addColums(column.columName);
					switch (column.dbtype) {
					case INTEGER:
						if(object==null){
							values.put(column.columName , "");
						}else{
							values.put(column.columName, (Integer) object);
						}
						break;
					
					case STRING:
						if(object==null){
							values.put(column.columName, "");
						}else{
							values.put(column.columName, (String) object);
						}
						break;
					case SHORT:
						if(object==null){
							values.put(column.columName, "");
						}else{
							values.put(column.columName, (Short) object);
						}
						break;
					case DOUBLE:
					case FLOAT:
					case LONG:
					case ENUM:
					case BOOLEAN:
						if(object==null){
							values.put(column.columName, "");
						}else{
							values.put(column.columName, object.toString());
						}
						break;
					default:
						break;
					}

				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
			valueInfos.addValue(values);
		}
		return valueInfos;
	}

	
}
