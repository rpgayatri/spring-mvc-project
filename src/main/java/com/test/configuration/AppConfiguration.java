package com.test.configuration;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


import com.test.interceptor.RequestProcessingTimeInterceptor;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;


@Configuration
@EnableTransactionManagement
@ComponentScan({ "com.test"})
@EnableWebMvc
@PropertySource({ "classpath:persistence-mongo.properties",
		"classpath:application.properties" })
public class AppConfiguration extends WebMvcConfigurerAdapter{
	public AppConfiguration() {
		super();
	}
	
	@Autowired
	private Environment env;
	
	private static final Logger logger = Logger.getLogger(AppConfiguration.class);
	
	@Bean(name = "dataSourceClient")
	public MongoClient dataSource() {
		/*return new MongoClient(env.getProperty("mongo.url"),
				Integer.valueOf(env.getProperty("mongo.port")));*/
		String mongoUserName =  env
				.getProperty("mongo.userName");
		
		String mongoPassword = env
				.getProperty("mongo.password");
		
		String host = env
				.getProperty("mongo.url").toString();
		
		Object portOb = env
				.getProperty("mongo.port");
		Integer port = Integer.parseInt(portOb.toString());
		
		String db = env
				.getProperty("mongo.dataBase").toString();
		
		logger.info("username : "+mongoUserName + ", password : "+mongoPassword+", host: "+host);
		if(mongoPassword == null){
			return new MongoClient(env.getProperty("mongo.url"),
					Integer.valueOf(env.getProperty("mongo.port")));
		}
		MongoCredential credential = MongoCredential.createCredential(mongoUserName, db , mongoPassword.toCharArray());
		MongoClient client = new MongoClient(new ServerAddress(host, port), Arrays.asList(credential));
		return client;
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new RequestProcessingTimeInterceptor());
	}
	
	@Override
	public void configureDefaultServletHandling(
			DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	@Override
	public void configureContentNegotiation(
			ContentNegotiationConfigurer configurer) {
		configurer.favorPathExtension(false);
	}
}
