package com.github.ming6.automybatis;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface MybatisDao<M, ID extends Serializable> {
	
	public void insert(@Param("*") M model);
	public void update(@Param("*") M model);
	public void deleteById(@Param("id") ID id);
	public M selectById(@Param("id") ID id);
	public M selectOne(@Param("*") M condition);
	public List<M> selectList(@Param("*") M condition, @Param("_offset") int offset, @Param("_limit") int limit);
	public Long selectCount();
	public Long selectCount(@Param("*") M condition);
	
}