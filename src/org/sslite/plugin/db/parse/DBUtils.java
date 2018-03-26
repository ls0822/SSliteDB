package org.sslite.plugin.db.parse;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.sslite.plugin.db.model.ColumnInfo;
import org.sslite.plugin.db.model.DataType;
import org.sslite.plugin.db.model.TableInfo;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 用来对应数据库和bean类型之间的数据类型
 * 
 * @author song
 *
 */
class DBUtils {

	private static final String INT = "int";
	private static final String VARCHAR = "varchar";
	private static final String FLOAT = "float";
	private static final String BLOB = "blob";
//
//	/**
//	 * 通过DB数据类型，得到相对应的在sql语句中的字段字符串
//	 * @param type  db数据类型
//	 * @return 在sql语句中的字符串
//	 */
//	public static String getSqlFeildType(DataType type) {
//		String sqlType = "";
//		switch (type) {
//		case INTEGER:
//		case SHORT:
//			sqlType = INT;
//			break;
//		case LONG:
//		case STRING:
//		case BOOLEAN: 
//		case ENUM:
//		case FLOAT:
//		case DOUBLE:
//			sqlType = VARCHAR;
//			break;
//		case UNKOWN:
//			break;
//		default:
//			sqlType = VARCHAR;
//			break;
//		}
//		return sqlType;
//	}

	/**
	 * 通过field的type类型名字，得到存到数据库里对应的数据类型
	 * @param typeName
	 * @return
	 */
	public static DataType getType(String typeName) {
		if (long.class.getName().equals(typeName) || Long.class.getName().equals(typeName)) {
			return DataType.LONG;
		} else if (Integer.class.getName().equals(typeName) || int.class.getName().equals(typeName)) {
			return DataType.INTEGER;
		} else if (String.class.getName() == typeName) {
			return DataType.STRING;
		} else if (float.class.getName().equals(typeName) || Float.class.getName().equals(typeName)) {
			return DataType.FLOAT;
		} else if (boolean.class.getName().equals(typeName) || Boolean.class.getName().equals(typeName)) {
			return DataType.BOOLEAN;
		} else if (short.class.getName().equals(typeName) || Short.class.getName().equals(typeName)) {
			return DataType.SHORT;
		} else {
			return DataType.ENUM;
		}
	}

//	/**
//	 * 通过对象得到对应的内容数据，并把这些数据分装到TableInfo中
//	 * @param tableinfo 表信息
//	 * @param obj 对象
//	 */
//	public static void getContentValues(TableInfo tableinfo, Object obj) {
//		List<Object> objs = new ArrayList<Object>();
//		objs.add(obj);
//		getBatchContentValues(tableinfo, objs);
//	}
//	/**
//	 * 通过对象得到对应的内容数据，并把这些数据分装到TableInfo中
//	 * @param tableinfo 表信息
//	 * @param obj 对象
//	 */
//	public static <T> void getBatchContentValues(TableInfo tableinfo, List<T> objs) {
//
//		List<ContentValues> contentValues = null;
//		for (Object obj : objs) {
//			ContentValues values = null;
//			Class<? extends Object> clazz = obj.getClass();
//			Field[] fields = ReflectUtils.getFields(clazz);
//
//			for (Field field : fields) {
//				field.setAccessible(true);
//				try {
//					if (values == null) {
//						values = new ContentValues();
//					}
//					field.setAccessible(true);
//					ColumnInfo column = tableinfo.getColumn(field.getName());
//					if (column == null) {
//						continue;
//					}
//					Object object = field.get(obj);
//					
//					switch (column.dbtype) {
//					case INTEGER:
//						if(object==null){
//							values.put(column.columName, "");
//						}else{
//							values.put(column.columName, (Integer) object);
//						}
//						break;
//					
//					case STRING:
//						if(object==null){
//							values.put(column.columName, "");
//						}else{
//							values.put(column.columName, (String) object);
//						}
//						break;
//					case SHORT:
//						if(object==null){
//							values.put(column.columName, "");
//						}else{
//							values.put(column.columName, (Short) object);
//						}
//						break;
//					case DOUBLE:
//					case FLOAT:
//					case LONG:
//					case ENUM:
//					case BOOLEAN:
//						if(object==null){
//							values.put(column.columName, "");
//						}else{
//							values.put(column.columName, object.toString());
//						}
//						break;
//					default:
//						break;
//					}
//
//				} catch (IllegalAccessException e) {
//					e.printStackTrace();
//				} catch (IllegalArgumentException e) {
//					e.printStackTrace();
//				}
//			}
//			if(contentValues==null){
//				contentValues = new ArrayList<ContentValues>();
//			}
//			contentValues.add(values);
//		}
//		tableinfo.contentValues = contentValues;
//		
//		
//	}
//	/**
//	 * 填充对象，把查询到的数据封装成对象
//	 * @param clazz 要封装成的对象的class
//	 * @param colunmMap 列表映射
//	 * @param cursor  数据库cursor
//	 * @return  返回封装好的对象
//	 * @throws Exception
//	 */
//	public static <T> List<T> getFillObjects(Class<T> clazz,HashMap<String, ColumnInfo> colunmMap ,Cursor cursor) throws Exception{
//		ArrayList<T> list = null;
//		if (cursor != null && cursor.getCount() > 0) {
//			if(list == null){
//				list = new ArrayList<T>();
//			}
//			while (cursor.moveToNext()) {
//
//				T obj = clazz.newInstance();
//				Set<String> keySet = colunmMap.keySet();
//				for (String key : keySet) {
//					DBUtils.fillValue(obj, colunmMap.get(key), cursor);
//				}
//				list.add(obj);
//			}
//		}
//		return list;
//	}
//
//	/**
//	 * 把字段数据填充到对象中
//	 * @param obj
//	 * @param columnInfo
//	 * @param cursor
//	 */
//	public static void fillValue(Object obj, ColumnInfo columnInfo, Cursor cursor) {
//		int columnIndex = cursor.getColumnIndex(columnInfo.columName);
//		int type = cursor.getType(columnIndex);
//		switch (type) {
//		case Cursor.FIELD_TYPE_INTEGER:
//			ReflectUtils.setFieldValue(obj, columnInfo.fieldName, cursor.getInt(columnIndex));
//			break;
//		case Cursor.FIELD_TYPE_FLOAT:
//			ReflectUtils.setFieldValue(obj, columnInfo.fieldName, cursor.getFloat(columnIndex));
//			break;
//		case Cursor.FIELD_TYPE_STRING:
//			String tmpValue = cursor.getString(columnIndex);
//			if (tmpValue == null) {
//				ReflectUtils.setFieldValue(obj, columnInfo.fieldName, null);
//				break;
//			}
//			switch (columnInfo.dbtype) {
//			case ENUM:
//				Class clazz = ReflectUtils.getClazzByName(columnInfo.fieldtype);
//				Field[] fields = ReflectUtils.getFields(clazz);
//				if (fields == null) {
//					ReflectUtils.setFieldValue(obj, columnInfo.fieldName, null);
//					break;
//				}
//				for (Field field : fields) {
//					if (field.getName().equals(tmpValue)) {
//						Enum value = Enum.valueOf(clazz, tmpValue);
//						ReflectUtils.setFieldValue(obj, columnInfo.fieldName, value);
//						return;
//					}
//				}
//				ReflectUtils.setFieldValue(obj, columnInfo.fieldName, null);
//				break;
//			case BOOLEAN:
//				if (ContantValue.BOOL.TRUE.equals(tmpValue)) {
//					ReflectUtils.setFieldValue(obj, columnInfo.fieldName, true);
//				} else {
//					ReflectUtils.setFieldValue(obj, columnInfo.fieldName, false);
//				}
//				break;
//			case LONG:
//				ReflectUtils.setFieldValue(obj, columnInfo.fieldName, Long.valueOf(tmpValue));
//				break;
//			case DOUBLE:
//				ReflectUtils.setFieldValue(obj, columnInfo.fieldName, Double.valueOf(tmpValue));
//				break;
//			case FLOAT:
//				ReflectUtils.setFieldValue(obj, columnInfo.fieldName, Float.valueOf(tmpValue));
//				break;		
//			default:
//				ReflectUtils.setFieldValue(obj, columnInfo.fieldName, tmpValue);
//				break;
//			}
//			break;
//		case Cursor.FIELD_TYPE_BLOB:
//			ReflectUtils.setFieldValue(obj, columnInfo.fieldName, cursor.getBlob(columnIndex));
//			break;
//		case Cursor.FIELD_TYPE_NULL:
//
//			break;
//		default:
//			break;
//		}
//	}
}
