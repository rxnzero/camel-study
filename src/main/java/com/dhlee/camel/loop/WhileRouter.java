package com.dhlee.camel.loop;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhileRouter extends RouteBuilder {
	
	private final Logger logger = LoggerFactory.getLogger(WhileRouter.class);
	
	public static void main(String[] args) throws Exception {
		WhileRouter loopRouter = new WhileRouter();		
		loopRouter.run();
		
	}
	private void run() throws Exception {
		System.setProperty("org.apache.camel.jmx.disabled", "true");
		
		DefaultCamelContext ctx = new DefaultCamelContext();
		
		ctx.addRoutes(this);
		ctx.start();
		
		ProducerTemplate producer = ctx.createProducerTemplate();
		String result = (String)producer.requestBody("direct:start", "");
		
		ctx.stop();
		
		logger.info("Main result = " + result);
	}
	
	@Override
	public void configure() throws Exception {
		from("direct:start")
		.loopDoWhile(simple("${body.length} <= 5"))
	        .log("loop : ${body}")
	        .to("mock:while")
	        .transform(body().append("A"))
	    .end()
	    .log("result : ${body}")
	    .to("mock:result");
	}

}
