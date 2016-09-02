package com.github.ming6.automybatis;

import java.lang.reflect.Proxy;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

public class MybatisDaoFactoryBean implements FactoryBean<MybatisDao<?, ?>> {
	
	@Autowired
	private SqlSessionTemplate sessionTemplate;
	
	private String className;
	
	public MybatisDaoFactoryBean(String className){
		this.className = className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	@Override
	public MybatisDao<?, ?> getObject() throws Exception {
		return (MybatisDao<?, ?>) Proxy.newProxyInstance(MybatisDaoFactoryBean.class.getClassLoader(), new Class[]{ Class.forName(className) }, new MybatisDaoInvocationHandler(sessionTemplate, className));
	}

	@Override
	public Class<?> getObjectType() {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}