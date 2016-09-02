package com.github.ming6.automybatis.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.IfSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.TextSqlNode;
import org.apache.ibatis.session.Configuration;

import com.github.ming6.automybatis.sql.SqlBuilder;

public class MappedStatementFactory {
	
	private static final Map<String, MappedStatement> mappedStatements = new HashMap<>();
	
	public static final void register(String key, MappedStatement statement){
		mappedStatements.put(key, statement);
	}
	
	public static final void registerAllDefault(Configuration configuration){
		for(Entry<String, MappedTable> entry : MappedTableFactory.getAll().entrySet()){
			registerDefault(configuration, entry.getKey());
		}
	}
	
	public static final void registerDefault(Configuration configuration, String tableKey){
		MappedTable table = MappedTableFactory.get(tableKey);
		Class<?> modelClass = null;
		try {
			modelClass = Class.forName(table.getClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		registerResultMap(configuration, tableKey, table, modelClass);
		registerInsertStatement(configuration, tableKey, table, modelClass);
		registerUpdateStatement(configuration, tableKey, table, modelClass);
		registerSelectStatement(configuration, tableKey, table, modelClass);
	}
	
	public static final void registerResultMap(Configuration configuration, String tableKey, MappedTable table, Class<?> modelClass){
		List<ResultMapping> resultMappings = new ArrayList<>();
		for(MappedColumn column : table.getColumns()){
			ResultMapping resultMapping = new ResultMapping.Builder(configuration, column.getFieldName(), column.getColumnName(), column.getFieldType()).build();
			resultMappings.add(resultMapping);
		}
		ResultMap resultMap = new ResultMap.Builder(configuration, "RM_"+modelClass.getSimpleName(), modelClass, resultMappings).build();
		configuration.addResultMap(resultMap);
	}
	
	private static final void registerInsertStatement(Configuration configuration, String tableKey, MappedTable table, Class<?> modelClass){
		String statementKey = tableKey + ".insert";
		RawSqlSource sqlSource = new RawSqlSource(configuration, SqlBuilder.getInsertSQL(table), modelClass);
	    MappedStatement statement = new MappedStatement.Builder(configuration, statementKey, sqlSource, SqlCommandType.INSERT).build();
	    configuration.addMappedStatement(statement);
	}
	private static final void registerUpdateStatement(Configuration configuration, String tableKey, MappedTable table, Class<?> modelClass){
		String statementKey = tableKey + ".update";
		RawSqlSource sqlSource = new RawSqlSource(configuration, SqlBuilder.getUpdateSQL(table), modelClass);
	    MappedStatement statement = new MappedStatement.Builder(configuration, statementKey, sqlSource, SqlCommandType.UPDATE).build();
	    configuration.addMappedStatement(statement);
	}
	private static final void registerSelectStatement(Configuration configuration, String tableKey, MappedTable table, Class<?> modelClass){
		String statementKey = tableKey + ".selectList";
		SqlNode sqlNode = new TextSqlNode(SqlBuilder.getSelectListSQL(table));
		DynamicSqlSource sqlSource = new DynamicSqlSource(configuration, sqlNode);
		
	    MappedStatement statement = new MappedStatement.Builder(configuration, statementKey, sqlSource, SqlCommandType.SELECT).build();
	    configuration.addMappedStatement(statement);
	}
}