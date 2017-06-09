package com.dhlee.camel.pattern;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChoiceStopRouter extends RouteBuilder {
	
	private final Logger logger = LoggerFactory.getLogger(ChoiceStopRouter.class);
	
	public static void main(String[] args) throws Exception {
		ChoiceStopRouter loopRouter = new ChoiceStopRouter();		
		loopRouter.run();
		
	}
	private void run() throws Exception {
		System.setProperty("org.apache.camel.jmx.disabled", "true");
		
		DefaultCamelContext ctx = new DefaultCamelContext();
		
		ctx.addRoutes(this);
		ctx.start();
		
		ProducerTemplate producer = ctx.createProducerTemplate();
		String result = (String) producer.requestBody("direct:start", "Bye");	 	
		ctx.stop();
		
		logger.info("Main result = " + result);
	}
	
	@Override
	public void configure() throws Exception {
		from("direct:start")
	    .choice()
	        .when(body().contains("Hello")).to("mock:hello")
	        .when(body().contains("Bye")).to("mock:bye").stop()
	        .otherwise().to("mock:other")
	    .end()
	    .to("mock:result"); // skipped when 'Bye'

	}

}
