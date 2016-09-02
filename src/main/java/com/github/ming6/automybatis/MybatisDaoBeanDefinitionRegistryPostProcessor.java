package com.github.ming6.automybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import com.github.ming6.automybatis.mapping.MappedStatementFactory;

public class MybatisDaoBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	private String basePackages;
	private SqlSessionFactory factory;
	
	public MybatisDaoBeanDefinitionRegistryPostProcessor(String basePackages, SqlSessionFactory factory){
		this.basePackages = basePackages;
		this.factory = factory;
	}
	
	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		
	}

	@Override
	public void postProcessBeanDefinitionRegistry(
			BeanDefinitionRegistry registry) throws BeansException {
		MybatisDaoBeanDefinitionScanner scanner = new MybatisDaoBeanDefinitionScanner(registry);
		scanner.scan(basePackages);
		MappedStatementFactory.registerAllDefault(factory.getConfiguration());
	}
}