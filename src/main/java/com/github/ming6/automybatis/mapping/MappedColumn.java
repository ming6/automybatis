package com.github.ming6.automybatis.mapping;

public class MappedColumn {
	
	private String columnName;
	private String fieldName;
	private Class<?> fieldType;
	private boolean isPk;
	
	public MappedColumn(String columnName, String fieldName){
		this.columnName = columnName;
		this.fieldName = fieldName;
	}
	
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public Class<?> getFieldType() {
		return fieldType;
	}
	public void setFieldType(Class<?> fieldType) {
		this.fieldType = fieldType;
	}
	public boolean getIsPk() {
		return isPk;
	}
	public void setIsPk(boolean isPk) {
		this.isPk = isPk;
	}
}