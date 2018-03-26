package org.sslite.plugin.db.model;

import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.text.TextUtils;

public class TableInfo {
	public String clazzName;
	public long maxCount ;
//	public String sql ;
	public String tableName ;
	public String primaryKey;
	public HashMap<String, ColumnInfo> colunmMap;//数据库的列名与列信息的映射
//	public HashMap<String, String> fieldMap;//字段名与列名的映射
//	public List<String> columnOrder;//字段名与列名的映射
	
//	public List<ContentValues> contentValues;
	
	
	public ColumnInfo getColumn(String fieldName){
		if(colunmMap==null){
			return null;
		}
		return colunmMap.get(fieldName);
	}
	
	public ColumnInfo getColumnByColunmName(String colunmName){
		if(colunmMap==null){
			return null;
		}
		return colunmMap.get(colunmName);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TableInfo [clazzName=" + clazzName + ", maxCount=" + maxCount + ", tableName=" + tableName
				+ ", primaryKey=" + primaryKey + ", colunmMap=" + colunmMap + "]";
	}
	
//	public ContentValues getSingleContentValues(){
//		if(contentValues==null){
//			return null;
//		}
//		return contentValues.get(0);
//	}
	
	
	
	
	
	
	
}
