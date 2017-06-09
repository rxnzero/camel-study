package com.dhlee.camel.jdbc;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.dataset.SimpleDataSet;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class JdbcInsertRoute  extends RouteBuilder {
	
	public static void main(String[] args) throws Exception {
		JdbcInsertRoute jdbc = new JdbcInsertRoute();
		jdbc.run();
	}
	
	private void run() throws Exception {
		System.setProperty("org.apache.camel.jmx.disabled", "true");
		
		DefaultCamelContext ctx = new DefaultCamelContext();
		SimpleRegistry  registry = new SimpleRegistry ();
		
		SimpleDataSet  simpleGenerator = new SimpleDataSet ();
		simpleGenerator.setSize(1);
		
		Map<String, Object> defaultHeaders = new HashMap<String, Object>();
		
		defaultHeaders.put("id", "dhlee");
		defaultHeaders.put("name", "Lee DongHoon");
		
		simpleGenerator.setDefaultHeaders(defaultHeaders);
		
		registry.put("simpleGenerator", simpleGenerator);
		
		DriverManagerDataSource mariadb = new DriverManagerDataSource();
		mariadb.setDriverClassName("org.mariadb.jdbc.Driver");
		mariadb.setUrl("jdbc:mariadb://localhost:4406/test");
		mariadb.setUsername("root");
		mariadb.setPassword("mariadb");
		
		registry.put("mariadb", mariadb);
		
		ctx.setRegistry(registry);
		ctx.addRoutes(this);
		ctx.start();
		
		ctx.stop();
		
	}
	@Override
	public void configure() throws Exception {
		from("dataset:simpleGenerator").
		setBody().constant("insert into user(id, name) values(:?id, :?name)").
		to("jdbc:mariadb?useHeadersAsParameters=true").
		log("log:insertLog?showHeaders=true");
	}

}
