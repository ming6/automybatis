package com.github.ming6.automybatis;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;

/**
 * 
 * */
public class MybatisDaoInvocationHandler implements InvocationHandler {
	
	private SqlSessionTemplate sessionTemplate;
	private String className;
	
	public MybatisDaoInvocationHandler(SqlSessionTemplate sessionTemplate, String className){
		this.sessionTemplate = sessionTemplate;
		this.className = className;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Map<String, Object> params = getParams(method, args);
		String statement = className + "." + method.getName();
		if(method.getName().startsWith("insert")){
			return sessionTemplate.insert(statement, params);
		}else if(method.getName().startsWith("update")){
			return sessionTemplate.update(statement, params);
		}else if(method.getName().startsWith("delete")){
			return sessionTemplate.delete(statement, params);
		}else if(method.getName().startsWith("selectList")){
			if(params.containsKey("_limit") && params.containsKey("_offset")){
				return sessionTemplate.selectList(statement, params, new RowBounds(Integer.parseInt(""+params.get("_offset")), Integer.parseInt("" + params.get("_limit"))));
			}else{
				return sessionTemplate.selectList(statement, params);
			}
		}else if(method.getName().startsWith("select")){
			return sessionTemplate.selectOne(statement, params);
		}else{
			return null;
		}
	}
	
	private Map<String, Object> getParams(Method method, Object[] args){
		Map<String, Object> params = new HashMap<>();
		int i = 0;
		for(Annotation[] annos : method.getParameterAnnotations()){
			for(Annotation anno : annos){
				if(anno instanceof Param){
					Param param = (Param) anno;
					if("*".equals(param.value())){
						for(Field field : args[i].getClass().getDeclaredFields()){
							field.setAccessible(true);
							try {
								params.put(field.getName(), field.get(args[i]));
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} finally{
								field.setAccessible(false);
							}
						}
					}else{
						params.put(param.value(), args[i]);
					}
				}
			}
			i++;
		}
		return params;
	}
}