package com.github.ming6.automybatis.config;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallFilter;
import com.github.ming6.automybatis.MybatisDaoBeanDefinitionRegistryPostProcessor;

@Configuration
@PropertySource("classpath:bootstrap.properties")
public class MybatisConfig implements EnvironmentAware, ApplicationContextAware {
	
	private String dbType;
	private String dbUrl;
	private String dbUsername;
	private String dbPassword;
	private String mapperLocations;
	private String typeAliasesPackage;
	private String scanPackage;
	
	@Override
	public void setEnvironment(Environment env) {
		this.dbType = env.getProperty("auto.mybatis.db.type");
		this.dbUrl = env.getProperty("auto.mybatis.db.url");
		this.dbUsername = env.getProperty("auto.mybatis.db.username");
		this.dbPassword = env.getProperty("auto.mybatis.db.password");
		//"classpath*:cn/bidlink/**/mapper/*.xml"
		this.mapperLocations = env.getProperty("auto.mybatis.mapper.locations");
		//"classpath:cn.bidlink.**.model"
		this.typeAliasesPackage = env.getProperty("auto.mybatis.type.aliases.package");
		this.scanPackage = env.getProperty("auto.mybatis.scan.package");
	}
	
	private ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Bean(initMethod="init", destroyMethod="close")
	public DataSource dataSource(){
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDbType(dbType);
		dataSource.setUrl(dbUrl);
		dataSource.setUsername(dbUsername);
		dataSource.setPassword(dbPassword);
		dataSource.setMaxActive(20);
		dataSource.setInitialSize(1);
		dataSource.setMaxWait(60000);
		dataSource.setMinIdle(1);
		dataSource.setTimeBetweenEvictionRunsMillis(3000);
		dataSource.setMinEvictableIdleTimeMillis(300000);
		dataSource.setValidationQuery("SELECT 'x'");
		dataSource.setTestWhileIdle(true);
		dataSource.setTestOnBorrow(false);
		dataSource.setTestOnReturn(false);
		//打开PSCache，并且指定每个连接上PSCache的大小
		dataSource.setPoolPreparedStatements(true);
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
		//是否打开removeAbandoned功能，对于建立时间超过removeAbandonedTimeout的连接强制关闭
		dataSource.setRemoveAbandoned(false);
		/**
		 * 打开removeAbandoned功能
		 * */
		//多少秒删除连接，秒为单位，指定连接建立多长时间就需要被强制关闭，如果removeAbandoned为false，这个设置项不再起作用
		dataSource.setRemoveAbandonedTimeout(7200);
		//关闭abanded连接时输出错误日志，指定发生removeAbandoned的时候，是否记录当前线程的堆栈信息到日志中
		dataSource.setLogAbandoned(true);
		//配置监控统计拦截的filters
		try {
			dataSource.setFilters("stat,log4j");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		List<Filter> filters = new ArrayList<>();
		StatFilter statFilter = new StatFilter();
		statFilter.setSlowSqlMillis(10000);
		statFilter.setLogSlowSql(true);
		statFilter.setMergeSql(true);
		filters.add(statFilter);
		
		WallFilter wallFilter = new WallFilter();
		filters.add(wallFilter);
		
		Slf4jLogFilter slf4jLogFilter = new Slf4jLogFilter();
		slf4jLogFilter.setResultSetLogEnabled(true);
		slf4jLogFilter.setStatementExecutableSqlLogEnable(true);
		filters.add(slf4jLogFilter);
		dataSource.setProxyFilters(filters);
		return dataSource;
	}
	
	@Bean
	public SqlSessionFactory sqlSessionFactory(){
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		factory.setDataSource(dataSource());
		try {
			factory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
		} catch (IOException e) {
			e.printStackTrace();
		}
		factory.setTypeAliasesPackage(typeAliasesPackage);
//		MybatisDaoLimitInterceptor limitor = new MybatisDaoLimitInterceptor();
//		factory.setPlugins(new Interceptor[]{ limitor });
		try {
			return factory.getObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Bean
	SqlSessionTemplate sqlSessionTemplate(){
		SqlSessionFactory factory = applicationContext.getBean(SqlSessionFactory.class);
		return new SqlSessionTemplate(factory);
	}
	
	@Bean
	MybatisDaoBeanDefinitionRegistryPostProcessor mybatisDaoBeanDefinitionRegistryPostProcessor(){
		SqlSessionFactory factory = applicationContext.getBean(SqlSessionFactory.class);
		return new MybatisDaoBeanDefinitionRegistryPostProcessor(scanPackage, factory);
	}
	
	@Bean
	PlatformTransactionManager platformTransactionManager(){
		DataSource dataSource = applicationContext.getBean(DataSource.class);
		DataSourceTransactionManager manager = new DataSourceTransactionManager();
		manager.setDataSource(dataSource);
		return manager;
	}
}