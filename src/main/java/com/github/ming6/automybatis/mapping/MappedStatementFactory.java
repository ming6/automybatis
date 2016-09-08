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
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandlerRegistry;

import com.github.ming6.automybatis.sql.SqlBuilder;

public class MappedStatementFactory {
	
	private static final Map<String, MappedStatement> mappedStatements = new HashMap<>();
	
	private static final LanguageDriver languageDriver = new XMLLanguageDriver();
	
	private static final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();
	private static final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();
	
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
		List<ResultMap> resultMaps = new ArrayList<>();
		ResultMap resultMap = registerResultMap(configuration, tableKey, table, modelClass);
		resultMaps.add(resultMap);
		registerInsertStatement(configuration, tableKey, table, modelClass);
		registerUpdateStatement(configuration, tableKey, table, modelClass);
		registerSelectStatement(configuration, tableKey, table, modelClass, resultMaps);
	}
	
	public static final ResultMap registerResultMap(Configuration configuration, String tableKey, MappedTable table, Class<?> modelClass){
		List<ResultMapping> resultMappings = new ArrayList<>();
		for(MappedColumn column : table.getColumns()){
			ResultMapping resultMapping = new ResultMapping.Builder(configuration, column.getFieldName(), column.getColumnName(), typeHandlerRegistry.getTypeHandler(column.getFieldType())).build();
			resultMappings.add(resultMapping);
		}
		ResultMap resultMap = new ResultMap.Builder(configuration, "RM_"+modelClass.getSimpleName(), modelClass, resultMappings).build();
		configuration.addResultMap(resultMap);
		return resultMap;
	}
	
	private static final MappedStatement registerInsertStatement(Configuration configuration, String tableKey, MappedTable table, Class<?> modelClass){
		String statementKey = tableKey + ".insert";
		RawSqlSource sqlSource = new RawSqlSource(configuration, SqlBuilder.getInsertSQL(table), modelClass);
	    MappedStatement statement = new MappedStatement.Builder(configuration, statementKey, sqlSource, SqlCommandType.INSERT).build();
	    configuration.addMappedStatement(statement);
	    return statement;
	}
	
	private static final MappedStatement registerUpdateStatement(Configuration configuration, String tableKey, MappedTable table, Class<?> modelClass){
		String statementKey = tableKey + ".update";
		RawSqlSource sqlSource = new RawSqlSource(configuration, SqlBuilder.getUpdateSQL(table), modelClass);
	    MappedStatement statement = new MappedStatement.Builder(configuration, statementKey, sqlSource, SqlCommandType.UPDATE).build();
	    configuration.addMappedStatement(statement);
	    return statement;
	}
	
	private static final MappedStatement registerSelectStatement(Configuration configuration, String tableKey, MappedTable table, Class<?> modelClass, List<ResultMap> resultMaps){
		String statementKey = tableKey + ".selectList";
		SqlSource sqlSource = languageDriver.createSqlSource(configuration, SqlBuilder.getSelectListSQL(table), modelClass);
		MappedStatement statement = new MappedStatement.Builder(configuration, statementKey, sqlSource, SqlCommandType.SELECT).resultMaps(resultMaps).build();
	    configuration.addMappedStatement(statement);
	    return statement;
	}
}