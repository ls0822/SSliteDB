package org.sslite.plugin.db.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.sslite.plugin.annotation.Column;
import org.sslite.plugin.annotation.PrimaryKey;
import org.sslite.plugin.db.model.ColumnInfo;
import org.sslite.plugin.db.model.DBValues;
import org.sslite.plugin.db.model.ContantValue.DBTYPE_FEILD_STR;
import org.sslite.plugin.db.model.DataType;
import org.sslite.plugin.db.model.OrderBy;
import org.sslite.plugin.db.model.TableInfo;
import org.sslite.plugin.db.parse.DBInfoManager;

import android.content.ContentValues;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;

public class SQLCreator {
	
	
	private static final int MAX_BATCH_INSERT_NUM = 500;

	private void Logd(String string) {
		
	}
	
	public  List<String> createTables(List<TableInfo> infos) {	
		
		List<String> sqls = new ArrayList<String>();
		for (TableInfo info : infos) {
			String sql = createTableSql(info);
			sqls.add(sql);
		}
		return sqls;
	}
	
	private  String createTableSql(TableInfo info) {
		if (info == null || info.colunmMap ==  null || info.colunmMap.keySet() == null) {
			return null;
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE IF NOT EXISTS ");
		sql.append(info.tableName);
		sql.append(" (");
		
		
		Set<String> keySet = info.colunmMap.keySet();
		for (String key : keySet) {
			ColumnInfo columnInfo = info.colunmMap.get(key);
			if (columnInfo == null) {
				continue;
			}
			sql.append(" ");
			sql.append(columnInfo.columName);
			sql.append(" ");
			sql.append(columnInfo.dbtype);
			
			if(!DataType.INTEGER.equals(columnInfo.dbtype)){
				if(columnInfo.columLength <= 0){
					return null;
				}
				sql.append("(");
				sql.append(columnInfo.columLength);
				sql.append(")");
			}
			
			if(columnInfo.isPrimaryKey){
				sql.append(" PRIMARY KEY");
			}
			sql.append(",");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(");");
		Logd("Sql str -> "+sql.toString());
		return sql.toString();
		
	}
	
	public  String upgradeTable() {
		return null;
	}
	
	public  String dropTable() {
		return  null;
	}
	
	public  <T>  List<String> insert(TableInfo tableInfo, DBValues dbValues) {
		
		List<ContentValues> contentValues = dbValues.getValues();
		int size = contentValues.size();
		int times = size / MAX_BATCH_INSERT_NUM;
		times = size % MAX_BATCH_INSERT_NUM == 0 ? times : times + 1;
		List<String> sqls = null;
		for (int i = 0; i < times; i++) {	
			if (sqls != null) {
				sqls = new ArrayList<String>();
			}
			List<ContentValues> subList = null;
			if(i==times-1){
				subList = contentValues.subList(i * MAX_BATCH_INSERT_NUM, contentValues.size());
			}else{
				subList = contentValues.subList(i * MAX_BATCH_INSERT_NUM,(i + 1) * MAX_BATCH_INSERT_NUM);
			}
			String sql = createInsertSql(tableInfo, dbValues.getOrderColums(), subList);
			sqls.add(sql);
		}
		return sqls;
	}
	
	
	
	private  <T> String createInsertSql(TableInfo tableInfo, List<String> columnOrder, List<ContentValues> contentValues){
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ");
		sql.append(tableInfo.tableName);
		sql.append("(");
		
		for (String columName : columnOrder) {
			sql.append(columName);
			sql.append(",");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		sql.append(" VALUES ");
		for (ContentValues value : contentValues) {
			sql.append("(");
			for (String key : columnOrder) {
				ColumnInfo column = tableInfo.getColumnByColunmName(key);
				DataType dbtype = column.dbtype;
				String type = dbtype.getSqlType();
				if(DBTYPE_FEILD_STR.VARCHAR.equals(type)){
					if(value.get(key)==null||"".equals(value.get(key))){
						sql.append("NULL");
					}else{
						sql.append("'");
						sql.append(value.get(key));
						sql.append("'");
					}
				}else{
					sql.append(value.get(key) ==null||"".equals(value.get(key))?"NULL":value.get(key));
				}
				sql.append(",");
			}
			sql.deleteCharAt(sql.length()-1);
			sql.append(" ),");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(";");
		Logd("batchInsertSql==>>"+sql.toString());
		return sql.toString();
	}
	
}
