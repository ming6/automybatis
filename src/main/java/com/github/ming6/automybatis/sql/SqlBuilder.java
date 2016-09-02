package com.github.ming6.automybatis.sql;

import org.apache.ibatis.scripting.xmltags.IfSqlNode;
import org.apache.ibatis.scripting.xmltags.TextSqlNode;

import com.github.ming6.automybatis.mapping.MappedColumn;
import com.github.ming6.automybatis.mapping.MappedTable;
import com.sun.javafx.binding.StringFormatter;

public final class SqlBuilder {
	
	private static final String SQL_EXPRESSION_EQUAL = "%s = #{%s}";
	
	public static final String getInsertSQL(MappedTable mappedTable){
		StringBuilder columnsSql = new StringBuilder();
		StringBuilder valuesSql = new StringBuilder();
		int i = 0;
		for(MappedColumn mappedColumn : mappedTable.getColumns()){
			columnsSql.append(mappedColumn.getColumnName());
			valuesSql.append("#").append("{").append(mappedColumn.getFieldName()).append("}");
			if(i < mappedTable.getColumns().size() - 1){
				columnsSql.append(",");
				valuesSql.append(",");
			}
			i++;
		}
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT").append(" ").append("INTO").append(" ");
		sql.append(mappedTable.getTableName()).append("(").append(columnsSql).append(")");
		sql.append(" ").append("VALUES").append("(").append(valuesSql).append(")");
		return sql.toString();
	}
	
	public static final String getUpdateSQL(MappedTable mappedTable){
		StringBuilder set = new StringBuilder();
		StringBuilder where = new StringBuilder();
		int i = 0;
		for(MappedColumn mappedColumn : mappedTable.getColumns()){
			set.append(mappedColumn.getColumnName()).append("=");
			set.append("#").append("{").append(mappedColumn.getFieldName()).append("}");
			if(i < mappedTable.getColumns().size() - 1){
				set.append(",");
			}
			if(mappedColumn.getIsPk()){
				where.append("WHERE").append(" ").append(mappedColumn.getColumnName());
				where.append("=").append("#").append("{").append(mappedColumn.getFieldName()).append("}");
			}
			i++;
		}
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE").append(" ").append(mappedTable.getTableName()).append(" ").append("SET");
		sql.append(" ").append(set).append(" ").append(where);
		return sql.toString();
	}
	
	public static final String getDeleteSQL(MappedTable mappedTable){
		return null;
	}
	
	public static final String getSelectListSQL(MappedTable mappedTable){
//		StringBuilder where = new StringBuilder("WHERE").append(" ");
//		int i = 0;
		
		for(MappedColumn mappedColumn : mappedTable.getColumns()){
			String expressionEqual = String.format(SQL_EXPRESSION_EQUAL, mappedColumn.getColumnName(), mappedColumn.getFieldName());
			String test = mappedColumn.getFieldName() + " != null";
			IfSqlNode ifSqlNode = new IfSqlNode(new TextSqlNode(expressionEqual), test);
			
//			i++;
		}
		
		StringBuilder sql = new StringBuilder();
//		sql.append("<script>");
		sql.append("SELECT").append(" ").append("*").append(" ");
		sql.append("FROM").append(" ").append(mappedTable.getTableName()).append(" ");
//		sql.append(where);
//		sql.append("</script>");
//		return sql.toString();
	}
}