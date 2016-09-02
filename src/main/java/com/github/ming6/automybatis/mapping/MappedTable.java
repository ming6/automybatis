package com.github.ming6.automybatis.mapping;

import java.util.ArrayList;
import java.util.List;

public class MappedTable {
	
	private String tableName;
	private String className;
	
	private final List<MappedColumn> columns = new ArrayList<>();
	
	public MappedTable(String tableName, String className){
		this.tableName = tableName;
		this.className = className;
	}
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public List<MappedColumn> getColumns() {
		return columns;
	}
	public void addMappedColumn(MappedColumn column){
		columns.add(column);
	}
}