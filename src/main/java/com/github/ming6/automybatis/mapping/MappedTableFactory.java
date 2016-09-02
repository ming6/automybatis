package com.github.ming6.automybatis.mapping;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

public class MappedTableFactory {
	
	private static final Map<String, MappedTable> mappedTables = new HashMap<>();
	
	public static final Map<String, MappedTable> getAll(){
		return mappedTables;
	}
	
	public static final MappedTable get(String key){
		return mappedTables.get(key);
	}
	
	public static final void register(String key, MappedTable mappedTable){
		mappedTables.put(key, mappedTable);
	}
	
	public static final void registerByJPA(String key, Class<?> clazz){
		Table table = clazz.getAnnotation(Table.class);
		MappedTable mappedTable = new MappedTable(table.name(), clazz.getName());
		for(Field field : clazz.getDeclaredFields()){
			field.setAccessible(true);
			Column column = field.getAnnotation(Column.class);
			if(column != null){
				MappedColumn mappedColumn = new MappedColumn(column.name(), field.getName());
				mappedColumn.setFieldType(field.getType());
				Id id = field.getAnnotation(Id.class);
				if(id != null){
					mappedColumn.setIsPk(true);
				}
				mappedTable.addMappedColumn(mappedColumn);
			}
		}
		mappedTables.put(key, mappedTable);
	}
}