package com.github.ming6.automybatis.sql;

import com.github.ming6.automybatis.mapping.MappedColumn;
import com.github.ming6.automybatis.mapping.MappedTable;

public final class SqlBuilder {
	
	private static final String SQL_INSERT = "insert into %s(%s) values(%s)";
	private static final String SQL_UPDATE = "update %s set %s where %s = #{%s}";
	
	private static final String SCRIPT_SELECT_LIST = "<script>select %s from %s where %s</script>";
	private static final String SCRIPT_WHERE_EXPRESSION = "<if test=\"%s != null\">%s = #{%s}</if>";
	
	public static final String getInsertSQL(MappedTable mappedTable){
		StringBuilder columns = new StringBuilder();
		StringBuilder values = new StringBuilder();
		int i = 0;
		for(MappedColumn mappedColumn : mappedTable.getColumns()){
			columns.append(mappedColumn.getColumnName());
			values.append("#").append("{").append(mappedColumn.getFieldName()).append("}");
			if(i < mappedTable.getColumns().size() - 1){
				columns.append(",");
				values.append(",");
			}
			i++;
		}
		return String.format(SQL_INSERT, mappedTable.getTableName(), columns.toString(), values.toString());
	}
	
	public static final String getUpdateSQL(MappedTable mappedTable){
		StringBuilder set = new StringBuilder();
		int i = 0;
		MappedColumn primaryKey = null;
		for(MappedColumn mappedColumn : mappedTable.getColumns()){
			set.append(mappedColumn.getColumnName()).append("=");
			set.append("#").append("{").append(mappedColumn.getFieldName()).append("}");
			if(i < mappedTable.getColumns().size() - 1){
				set.append(",");
			}
			if(mappedColumn.getIsPk()){
				primaryKey = mappedColumn;
			}
			i++;
		}
		return String.format(SQL_UPDATE, mappedTable.getTableName(), set.toString(), primaryKey.getColumnName(), primaryKey.getFieldName());
	}
	
	public static final String getDeleteSQL(MappedTable mappedTable){
		return null;
	}
	
	public static final String getSelectListSQL(MappedTable mappedTable){
		StringBuilder where = new StringBuilder();
		int i = 0;
		for(MappedColumn mappedColumn : mappedTable.getColumns()){
			String expressionEqual = String.format(SCRIPT_WHERE_EXPRESSION, mappedColumn.getFieldName(), mappedColumn.getColumnName(), mappedColumn.getFieldName());
			where.append(expressionEqual);
			if(i < mappedTable.getColumns().size() - 2){
				where.append(" ").append("AND").append(" ");
			}
			i++;
		}
		return String.format(SCRIPT_SELECT_LIST, "*", mappedTable.getTableName(), where.toString());
	}
}