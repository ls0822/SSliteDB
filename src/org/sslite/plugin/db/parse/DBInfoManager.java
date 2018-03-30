package org.sslite.plugin.db.parse;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.sslite.plugin.db.model.ColumnInfo;
import org.sslite.plugin.db.model.ContantValue;
import org.sslite.plugin.db.model.DBValues;
import org.sslite.plugin.db.model.DataType;
import org.sslite.plugin.db.model.TableInfo;
import org.sslite.plugin.log.LLog;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 主要用于数据的封住
 * 
 * @author song
 *
 */
public class DBInfoManager {

	public HashMap<String, TableInfo> tablesInfo;

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		if (tablesInfo != null) {
			tablesInfo.clear();
			tablesInfo = null;
		}
	}

	/**
	 * 得到所有的table信息
	 * 
	 * @return
	 */
	public synchronized List<TableInfo> getTablesInfo() {
		if (tablesInfo != null && tablesInfo.size() > 0) {
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
	 * 
	 * @param obj
	 */
	public synchronized TableInfo getTableInfo(Object obj) {
		if (tablesInfo == null) {
			tablesInfo = new HashMap<String, TableInfo>();
		}

		Class<?> clazz = ReflectUtils.getClazzByObj(obj);
		String tableName = AnnoParse.getTableName(clazz);
		String key = clazz.getName()+ tableName;
		if (tablesInfo.containsKey(key)) {
			return tablesInfo.get(key);
		}

		TableInfo tableInfo = initTable(clazz);
		if (tableInfo == null) {
			return null;
		}

		tablesInfo.put(key, tableInfo);
		return tableInfo;
	}

	/**
	 * 根据对象得到当前的table的信息
	 * 
	 * @param obj
	 */
	public synchronized TableInfo getTableInfo(Class<?> clazz) {
		if (tablesInfo == null) {
			tablesInfo = new HashMap<String, TableInfo>();
		}
		
		String tableName = AnnoParse.getTableName(clazz);
		
		String key = clazz.getName()+ tableName;
		if (tablesInfo.containsKey(key)) {
			return tablesInfo.get(key);
		}
		long time = System.currentTimeMillis();
		TableInfo tableInfo = initTable(clazz);
		LLog.d("initTable time = >" + (System.currentTimeMillis() - time));
		if (tableInfo == null) {
			return null;
		}

		tablesInfo.put(key, tableInfo);
		return tableInfo;
	}

	/**
	 * 根据传入对象初始化当前的对象的
	 * 
	 * @param clazz
	 */
	private TableInfo initTable(Class<?> clazz) {
		TableInfo initTableInfo = AnnoParse.initTableInfo(clazz);
		return initTableInfo;
	}

	public <T> ContentValues getContentValues(TableInfo tableinfo, T obj) {
		ContentValues values = null;
		Class<? extends Object> clazz = obj.getClass();
		Field[] fields = ReflectUtils.getFields(clazz);
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				if (values == null) {
					values = new ContentValues();
				}
				ColumnInfo column = tableinfo.getColumnByFieldName(field.getName());
				if (column == null) {
					continue;
				}
				Object object = field.get(obj);
				switch (column.dbtype) {
				case INTEGER:
					if (object == null) {
						values.put(column.columName, "");
					} else {
						values.put(column.columName, (Integer) object);
					}
					break;

				case STRING:
					if (object == null) {
						values.put(column.columName, "");
					} else {
						values.put(column.columName, (String) object);
					}
					break;
				case SHORT:
					if (object == null) {
						values.put(column.columName, "");
					} else {
						values.put(column.columName, (Short) object);
					}
					break;
				case DOUBLE:
				case FLOAT:
				case LONG:
				case ENUM:
				case BOOLEAN:
					if (object == null) {
						values.put(column.columName, "");
					} else {
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

		return values;
	}

	/**
	 * 通过对象得到对应的内容数据，并把这些数据分装到TableInfo中
	 * 
	 * @param tableinfo
	 *            表信息
	 * @param obj
	 *            对象
	 */
	public <T> DBValues getBatchContentValues(TableInfo tableinfo, List<T> objs) {
		DBValues valueInfos = null;
		Class<? extends Object> clazz = objs.get(0).getClass();
		Field[] fields = ReflectUtils.getFields(clazz);
		for (Field field : fields) {
			field.setAccessible(true);
			ColumnInfo column = tableinfo.getColumnByFieldName(field.getName());
			if (column == null) {
				continue;
			}
			if (valueInfos == null) {
				valueInfos = new DBValues();
			}
			valueInfos.addColums(column.columName);
		}
		for (Object obj : objs) {
			ContentValues values = getContentValues(tableinfo, obj);
			valueInfos.addValue(values);
		}
		return valueInfos;
	}

	public <T> List<T> packageData(Class<T> clazz, TableInfo tableInfo, Cursor cursor)
			throws InstantiationException, IllegalAccessException {
		ArrayList<T> list = null;
		if (cursor != null && cursor.getCount() > 0) {
			if (list == null) {
				list = new ArrayList<T>();
			}
			while (cursor.moveToNext()) {
				T obj = clazz.newInstance();
				Set<String> keySet = tableInfo.colunmMap.keySet();
				for (String key : keySet) {
					fillValue(obj, tableInfo.colunmMap.get(key), cursor);
				}
				list.add(obj);
			}
		}
		return list;
	}

	/**
	 * 把字段数据填充到对象中
	 * 
	 * @param obj
	 * @param columnInfo
	 * @param cursor
	 */
	private static void fillValue(Object obj, ColumnInfo columnInfo, Cursor cursor) {
		int columnIndex = cursor.getColumnIndex(columnInfo.columName);
		int type = cursor.getType(columnIndex);
		switch (type) {
		case Cursor.FIELD_TYPE_INTEGER:
			if (columnInfo.dbtype == DataType.STRING) {
				ReflectUtils.setFieldValue(obj, columnInfo.fieldName, String.valueOf(cursor.getInt(columnIndex)));
				break;
			}
			ReflectUtils.setFieldValue(obj, columnInfo.fieldName, cursor.getInt(columnIndex));
			break;
		case Cursor.FIELD_TYPE_FLOAT:
			ReflectUtils.setFieldValue(obj, columnInfo.fieldName, cursor.getFloat(columnIndex));
			break;
		case Cursor.FIELD_TYPE_STRING:
			String tmpValue = cursor.getString(columnIndex);
			if (tmpValue == null) {
				ReflectUtils.setFieldValue(obj, columnInfo.fieldName, null);
				break;
			}
			switch (columnInfo.dbtype) {
			case ENUM:
				Class clazz = ReflectUtils.getClazzByName(columnInfo.fieldtype);
				Field[] fields = ReflectUtils.getFields(clazz);
				if (fields == null) {
					ReflectUtils.setFieldValue(obj, columnInfo.fieldName, null);
					break;
				}
				for (Field field : fields) {
					if (field.getName().equals(tmpValue)) {
						Enum value = Enum.valueOf(clazz, tmpValue);
						ReflectUtils.setFieldValue(obj, columnInfo.fieldName, value);
						return;
					}
				}
				ReflectUtils.setFieldValue(obj, columnInfo.fieldName, null);
				break;
			case BOOLEAN:
				if (ContantValue.BOOL.TRUE.equals(tmpValue)) {
					ReflectUtils.setFieldValue(obj, columnInfo.fieldName, true);
				} else {
					ReflectUtils.setFieldValue(obj, columnInfo.fieldName, false);
				}
				break;
			case LONG:
				ReflectUtils.setFieldValue(obj, columnInfo.fieldName, Long.valueOf(tmpValue));
				break;
			case DOUBLE:
				ReflectUtils.setFieldValue(obj, columnInfo.fieldName, Double.valueOf(tmpValue));
				break;
			case FLOAT:
				ReflectUtils.setFieldValue(obj, columnInfo.fieldName, Float.valueOf(tmpValue));
				break;
			default:
				ReflectUtils.setFieldValue(obj, columnInfo.fieldName, tmpValue);
				break;
			}
			break;
		case Cursor.FIELD_TYPE_BLOB:
			ReflectUtils.setFieldValue(obj, columnInfo.fieldName, cursor.getBlob(columnIndex));
			break;
		case Cursor.FIELD_TYPE_NULL:

			break;
		default:
			break;
		}
	}

}
