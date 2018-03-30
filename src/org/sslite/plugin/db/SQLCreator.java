package org.sslite.plugin.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.sslite.plugin.db.model.ColumnInfo;
import org.sslite.plugin.db.model.ContantValue.DBTYPE_FEILD_STR;
import org.sslite.plugin.db.model.DBValues;
import org.sslite.plugin.db.model.DataType;
import org.sslite.plugin.db.model.OrderBy;
import org.sslite.plugin.db.model.TableInfo;
import org.sslite.plugin.log.LLog;

import android.content.ContentValues;

class SQLCreator {

	private static final int MAX_BATCH_INSERT_NUM = 500;

	public List<String> createTables(List<TableInfo> infos) {

		List<String> sqls = new ArrayList<String>();
		for (TableInfo info : infos) {
			String sql = createTableSql(info);
			sqls.add(sql);
		}
		return sqls;
	}

	private String createTableSql(TableInfo info) {
		if (info == null || info.colunmMap == null || info.colunmMap.keySet() == null) {
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
			sql.append(columnInfo.dbtype.getSqlType());

			if (!DataType.INTEGER.getSqlType().equals(columnInfo.dbtype.getSqlType())) {
				if (columnInfo.columLength <= 0) {
					return null;
				}
				sql.append("(");
				sql.append(columnInfo.columLength);
				sql.append(")");
			}

			if (columnInfo.isPrimaryKey) {
				sql.append(" PRIMARY KEY");
			}
			sql.append(",");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(");");
		LLog.d("Create tables sql -> " + sql.toString());
		return sql.toString();

	}

	public String upgradeTable() {
		return null;
	}

	public String dropTable() {
		return null;
	}

	public <T> List<String> insert(TableInfo tableInfo, DBValues dbValues) {

		List<ContentValues> contentValues = dbValues.getValues();
		int size = contentValues.size();
		int times = size / MAX_BATCH_INSERT_NUM;
		times = size % MAX_BATCH_INSERT_NUM == 0 ? times : times + 1;
		List<String> sqls = null;
		for (int i = 0; i < times; i++) {
			if (sqls == null) {
				sqls = new ArrayList<String>();
			}
			List<ContentValues> subList = null;
			if (i == times - 1) {
				subList = contentValues.subList(i * MAX_BATCH_INSERT_NUM, contentValues.size());
			} else {
				subList = contentValues.subList(i * MAX_BATCH_INSERT_NUM, (i + 1) * MAX_BATCH_INSERT_NUM);
			}
			String sql = createInsertSql(tableInfo, dbValues.getOrderColums(), subList);
			sqls.add(sql);
		}
		return sqls;
	}

	private <T> String createInsertSql(TableInfo tableInfo, List<String> columnOrder,
			List<ContentValues> contentValues) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ");
		sql.append(tableInfo.tableName);
		sql.append("(");

		for (String columName : columnOrder) {
			sql.append(columName);
			sql.append(",");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");
		sql.append(" VALUES ");
		for (ContentValues value : contentValues) {
			sql.append("(");
			for (String key : columnOrder) {
				ColumnInfo column = tableInfo.getColumnByColunmName(key);
				DataType dbtype = column.dbtype;
				String type = dbtype.getSqlType();
				if (DBTYPE_FEILD_STR.VARCHAR.equals(type)) {
					if (value.get(key) == null || "".equals(value.get(key))) {
						sql.append("NULL");
					} else {
						sql.append("'");
						sql.append(value.get(key));
						sql.append("'");
					}
				} else {
					sql.append(value.get(key) == null || "".equals(value.get(key)) ? "NULL" : value.get(key));
				}
				sql.append(",");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(" ),");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(";");
		LLog.d("Insert sql -> " + sql.toString());
		return sql.toString();
	}

	public List<String> delete(TableInfo tableInfo, DBValues dbValues) {
		List<ContentValues> values = dbValues.getValues();
		List<String> sqls = null;
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM ");
		sql.append(tableInfo.tableName);
		sql.append(" WHERE ");
		sql.append(tableInfo.primaryKey);
		sql.append(" IN (");
		for (ContentValues v : values) {
			sql.append(v.get(tableInfo.primaryKey));
			sql.append(",");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(");");

		LLog.d("Delete sql -> " + sql.toString());

		if (sqls == null) {
			sqls = new ArrayList<String>();
		}
		sqls.add(sql.toString());
		return sqls;
	}

	public List<String> deleteOrderDatas(TableInfo tableInfo, long count, OrderBy orderBy) {
		List<String> sqls = null;
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM ");
		sql.append(tableInfo.tableName);
		sql.append(" WHERE ");
		sql.append(tableInfo.primaryKey);
		sql.append(" IN (");
		sql.append("SELECT ");
		sql.append(tableInfo.primaryKey);
		sql.append(" FROM ");
		sql.append(tableInfo.tableName);
		sql.append(" ORDER BY ");
		sql.append(tableInfo.primaryKey);
		if (orderBy == null || OrderBy.ASC == orderBy) {
			sql.append(" ASC");
		} else {
			sql.append(" DESC");
		}
		sql.append(" LIMIT ");
		sql.append("0,");
		sql.append(count <= 0 ? 1 : count);
		sql.append(";");

		LLog.d("Delete Order Datas sql -> " + sql.toString());

		if (sqls == null) {
			sqls = new ArrayList<String>();
		}
		sqls.add(sql.toString());
		return sqls;
	}

	public String queryDataCount(TableInfo tableInfo) {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from ");
		String tableName = tableInfo.tableName;
		sql.append(tableName);
		LLog.d("Query count sql -> " + sql.toString());
		return sql.toString();
	}

	public String findObjectById(TableInfo tableInfo, ContentValues contentValues) {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from ");
		sql.append(tableInfo.tableName);
		sql.append(" where ");
		sql.append(tableInfo.primaryKey);
		sql.append(" = ");
		sql.append(contentValues.get(tableInfo.primaryKey));
		sql.append(";");
		LLog.d("Find obj sql -> " + sql.toString());
		return sql.toString();
	}

	public String find(TableInfo tableInfo, OrderBy orderBy, int limitStart, int limitSize) {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from ");
		String tableName = tableInfo.tableName;
		sql.append(tableName);
		if (orderBy != null) {
			sql.append(" order by ");
			String keyName = tableInfo.primaryKey;
			sql.append(keyName);
			if (orderBy == OrderBy.ASC) {
				sql.append(" asc");
			} else {
				sql.append(" desc");
			}
		}

		if (limitStart >= 0 && limitSize >= 0) {
			sql.append(" limit ");
			sql.append(limitStart);
			sql.append(",");
			sql.append(limitSize);
		}
		sql.append(";");
		LLog.d("find sql==>>" + sql.toString());
		return sql.toString();

	}

	public List<String> update(TableInfo tableInfo, DBValues dbValues) {
//		replace into test_tbl (id,dr) values (1,'2'),(2,'3'),...(x,'y');

		ArrayList<String> sqls = null;
		List<String> columnOrder = dbValues.getOrderColums();
		List<ContentValues> contentValues = dbValues.getValues();
		StringBuilder sql = new StringBuilder();
		sql.append("REPLACE INTO ");
		sql.append(tableInfo.tableName);
		sql.append("(");
		for (String columName : columnOrder) {
			sql.append(columName);
			sql.append(",");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");
		sql.append(" VALUES ");
		for (ContentValues value : contentValues) {
			sql.append("(");
			for (String key : columnOrder) {
				ColumnInfo column = tableInfo.getColumnByColunmName(key);
				DataType dbtype = column.dbtype;
				String type = dbtype.getSqlType();
				if (DBTYPE_FEILD_STR.VARCHAR.equals(type)) {
					if (value.get(key) == null || "".equals(value.get(key))) {
						sql.append("NULL");
					} else {
						sql.append("'");
						sql.append(value.get(key));
						sql.append("'");
					}
				} else {
					sql.append(value.get(key) == null || "".equals(value.get(key)) ? "NULL" : value.get(key));
				}
				sql.append(",");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(" ),");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(";");
		
		LLog.d("update sql==>>" + sql.toString());
		
		if (sqls == null) {
			sqls = new ArrayList<String>();
		}
		sqls.add(sql.toString());
		return sqls;
	}
//	public List<String> update(TableInfo tableInfo, DBValues dbValues) {
////		UPDATE categories 
////	    SET display_order = CASE id 
////	            WHEN 1 THEN 3 
////	            WHEN 2 THEN 4 
////	            WHEN 3 THEN 5 
////	        END, 
////	        title = CASE id 
////	            WHEN 1 THEN 'New Title 1'
////	            WHEN 2 THEN 'New Title 2'
////	            WHEN 3 THEN 'New Title 3'
////	        END
////	    WHERE id IN (1,2,3)
//		ArrayList<String> sqls = null;
//		List<String> orderColums = dbValues.getOrderColums();
//		List<ContentValues> values = dbValues.getValues();
//		StringBuilder sql = new StringBuilder();
//		sql.append("UPDATE ");
//		sql.append(tableInfo.tableName);
//		sql.append(" SET ");
//		for (String colum : orderColums) {
//			sql.append(colum);
//			sql.append(" = CASE ");
//			sql.append(tableInfo.primaryKey);
//			for (ContentValues v : values) {
//				sql.append(" WHEN ");
//				sql.append(v.get(tableInfo.primaryKey));
//				sql.append(" THEN ");
//				sql.append(v.get(colum));
//			}
//			sql.append(" END ,");
//		}
//		sql.deleteCharAt(sql.length() - 1);
//		sql.append(" WHERE ");
//		sql.append(tableInfo.primaryKey);
//		sql.append(" IN (");
//		for (ContentValues v : values) {
//			sql.append(v.get(tableInfo.primaryKey));
//			sql.append(",");
//		}
//		sql.deleteCharAt(sql.length() - 1);
//		sql.append(");");
//		
//		LLog.d("update sql==>>" + sql.toString());
//		
//		if (sqls == null) {
//			sqls = new ArrayList<String>();
//		}
//		sqls.add(sql.toString());
//		return sqls;
//	}

}
