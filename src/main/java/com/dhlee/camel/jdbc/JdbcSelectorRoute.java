package com.dhlee.camel.jdbc;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class JdbcSelectorRoute  extends RouteBuilder {
	
	public static void main(String[] args) throws Exception {
		JdbcSelectorRoute jdbc = new JdbcSelectorRoute();
		jdbc.run();
	}
	
	private void run() throws Exception {
		System.setProperty("org.apache.camel.jmx.disabled", "true");
		
		DefaultCamelContext ctx = new DefaultCamelContext();
		
		SimpleRegistry  registry = new SimpleRegistry ();
		registry.put("rowProcessor", new RowProcessor());
		
		DriverManagerDataSource mariadb = new DriverManagerDataSource();
		mariadb.setDriverClassName("org.mariadb.jdbc.Driver");
		mariadb.setUrl("jdbc:mariadb://localhost:4406/test");
		mariadb.setUsername("root");
		mariadb.setPassword("mariadb");
		
		registry.put("mariadb", mariadb);
		
		ctx.setRegistry(registry);
		ctx.addRoutes(this);
		ctx.start();
		
		Thread.sleep(10 * 1000);
		
		ctx.stop();
	}
	@Override
	public void configure() throws Exception {
		from("timer://timer1?period=10s").
		log("select from db table :: camelId = ${camelId}, exchangeId = ${exchangeId}, ").
		setBody().constant("SELECT id, name from user").
		to("jdbc:mariadb").  // ?readSize=100
		log("split result - ${header.CamelJdbcRowCount} rows").
		split().simple("${body}").
		log("process row (${header.CamelSplitIndex} / ${header.CamelSplitSize})  - ${body}").
		to("bean:rowProcessor").
		to("stream:out");
	}

}
