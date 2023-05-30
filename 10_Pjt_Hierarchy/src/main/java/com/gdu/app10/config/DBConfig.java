package com.gdu.app10.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@MapperScan(basePackages = {"com.gdu.app10.mapper"})         // @Mapper가 존재하는 패키지를 작성한다. 
@PropertySource(value={"classpath:application.properties"})  // applcation.properties 파일의 속성을 읽어 오자!   -> Environment 랑 세트라고 생각.
@EnableTransactionManagement(proxyTargetClass=true)          // 트랜잭션 처리를 허용한다. (DBConfig에서 앞으로도 계속 사용할 애너테이션)
@Configuration
public class DBConfig {

	@Autowired
	private Environment env; 
	
	// HikariConfig Bean - 환경 설정을 먼저 만들어줘야 한다. application.properties에 적혀있는 것을 읽는다(가져온다). (properties 파일로 나눠준 이유는 gitignore로 설정해서 올리지 않으려고)
	@Bean
	public HikariConfig hikariConfig() {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setDriverClassName(env.getProperty("spring.datasource.hikari.driver-class-name"));
		hikariConfig.setJdbcUrl(env.getProperty("spring.datasource.hikari.jdbc-url"));
		hikariConfig.setUsername(env.getProperty("spring.datasource.hikari.username"));
		hikariConfig.setPassword(env.getProperty("spring.datasource.hikari.password"));
		return hikariConfig;
	}
	
	// HikariDataSource Bean (자동완성의 두번째 생성자로 히카리컨피그 넣는 거 있다)
	// destroyMethod="close" : bean 객체의 스코프가 끝날을 경우(스프링에서는 어플리케이션 컨텍스트가 종료되었을 경우로 생각하면 되겠다.) class 속성에 선언한 클래스의 close 메서드를 호출하는 의미이다.
	@Bean(destroyMethod="close")
	public HikariDataSource hikariDataSource() {
		return new HikariDataSource(hikariConfig());
	}
	
	
	// SqlSessionFactory Bean
	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception { 
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(hikariDataSource());
		bean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource(env.getProperty("mybatis.config-location")));  
		bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(env.getProperty("mybatis.mapper-locations")));
		return bean.getObject();
	}
	
	// SqlSessionTemplate Bean (기존의 SqlSession)
	@Bean
	public SqlSessionTemplate sqlSessionTemplate() throws Exception { 
		return new SqlSessionTemplate(sqlSessionFactory()); 
	}
	
	
	// TransactionManager Bean - 트랜잭션 매니저는 datasource만 있으면 된다.
	@Bean
	public TransactionManager transactionManager() {
		return new DataSourceTransactionManager(hikariDataSource());
	}
	
	
	
}
