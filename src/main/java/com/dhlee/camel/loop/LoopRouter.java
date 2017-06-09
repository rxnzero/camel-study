package com.dhlee.camel.loop;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoopRouter extends RouteBuilder {
	
	private final Logger logger = LoggerFactory.getLogger(LoopRouter.class);
	
	public static void main(String[] args) throws Exception {
		LoopRouter loopRouter = new LoopRouter();		
		loopRouter.run();
		
	}
	private void run() throws Exception {
		System.setProperty("org.apache.camel.jmx.disabled", "true");
		
		DefaultCamelContext ctx = new DefaultCamelContext();
		
		ctx.addRoutes(this);
		ctx.start();
		
		ProducerTemplate producer = ctx.createProducerTemplate();
		String result = (String) producer.requestBody("direct:start", "");		
		ctx.stop();
		
		logger.info("Main result = " + result);
	}
	
	@Override
	public void configure() throws Exception {
		from("direct:start")
	    .loop(3).copy()
	        .transform(body().append("B"))
	        .log("loop : ${body}")
	        .to("mock:loop")
//	        .to("stream:out")
	    .end()
	    .log("result : ${body}")
	    .to("mock:result");
//	    .to("stream:out");
		
	}

}
