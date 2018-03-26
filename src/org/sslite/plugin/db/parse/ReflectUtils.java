package org.sslite.plugin.db.parse;

import java.lang.reflect.Field;

class ReflectUtils {

	/**
	 * Get the Class by classname
	 * 
	 * @param clazzName
	 * @return
	 */
	public static Class<?> getClazzByName(String clazzName) {
		try {
			Class<?> clazz = Class.forName(clazzName);
			return clazz;
		} catch (ClassNotFoundException e) {
			//TODO 没有类
			e.printStackTrace();
		}
		return null;
	}
	
	public static Class<?> getClazzByObj(Object obj) {
		Class<?> clazz = obj.getClass();
		return clazz;
	}

	public static Field[] getFields(String clazzName) {
		try {
			Class<?> clazz = Class.forName(clazzName);
			Field[] fields = clazz.getDeclaredFields();
			return fields;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Field[] getFields(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		return fields;
	}

	public static void setFieldValue(Object obj, String propertyName, Object value) {
		try {
			Field field = obj.getClass().getDeclaredField(propertyName);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
