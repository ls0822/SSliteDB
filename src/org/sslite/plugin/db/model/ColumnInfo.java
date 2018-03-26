package org.sslite.plugin.db.model;

/**
 * 列信息，存放列的相关信息
 * @author Liu Song
 */
public class ColumnInfo {
	
	public String fieldName; //字段名
	public String columName; //列名
	public String fieldtype; // 字段类型
	public int columLength; //列的长度
	
	public DataType dbtype;
	public boolean isPrimaryKey = false; 
	
	
	@Override
	public String toString() {
		return "ColumnInfo [fieldName=" + fieldName + ", columName=" + columName + ", fieldtype=" + fieldtype
				+ ", dbtype=" + dbtype + "]";
	}
	
	
}
