package com.github.ming6.automybatis;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.stereotype.Repository;

import com.github.ming6.automybatis.mapping.MappedTableFactory;

public class MybatisDaoBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {
	
	public MybatisDaoBeanDefinitionScanner(BeanDefinitionRegistry registry) {
		super(registry);
	}
	
	@Override
	protected boolean isCandidateComponent(
			AnnotatedBeanDefinition beanDefinition) {
		return true;
	}

	@Override
	protected boolean isCandidateComponent(MetadataReader metadataReader)
			throws IOException {
		ClassMetadata classMetadata = metadataReader.getClassMetadata();
		String className = classMetadata.getClassName();
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (clazz.isInterface()) {
            String simpleName = clazz.getSimpleName();
            simpleName = simpleName.substring(0, 1).toLowerCase().concat(simpleName.substring(1));
            try {
                BeanDefinition beanDefinition = getRegistry().getBeanDefinition(simpleName);
                if (beanDefinition == null && clazz.isAnnotationPresent(Repository.class)) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                if ( clazz.isAnnotationPresent(Repository.class)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
	}

	@Override
	public Set<BeanDefinition> findCandidateComponents(String basePackage) {
		Set<BeanDefinition> candidates = super.findCandidateComponents(basePackage);
		for(BeanDefinition candidate : candidates){
			String className = candidate.getBeanClassName();
			Class<?> interfaceClass = null;
			Class<?> modelClass = null;
			try {
				interfaceClass = Class.forName(className);
				modelClass = (Class<?>) ((ParameterizedType)interfaceClass.getGenericInterfaces()[0]).getActualTypeArguments()[0];
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			//MybatisDaoFactoryBean代理
		    candidate.setBeanClassName("com.github.ming6.automybatis.MybatisDaoFactoryBean");
		    candidate.setLazyInit(false);
		    candidate.getConstructorArgumentValues().addGenericArgumentValue(className);
		    //
		    MappedTableFactory.registerByJPA(className, modelClass);
		}
		return candidates;
	}
}