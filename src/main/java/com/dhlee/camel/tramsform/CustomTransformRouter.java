package com.dhlee.camel.tramsform;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.gson.GsonDataFormat;
import org.apache.camel.dataformat.bindy.fixed.BindyFixedLengthDataFormat;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.FieldNamingPolicy;

public class CustomTransformRouter extends RouteBuilder {
	
	private final Logger logger = LoggerFactory.getLogger(CustomTransformRouter.class);
	
	public static void main(String[] args) throws Exception {
		CustomTransformRouter router = new CustomTransformRouter();		
		router.run();
		
	}
	private void run() throws Exception {
		System.setProperty("org.apache.camel.jmx.disabled", "true");
		
		DefaultCamelContext ctx = new DefaultCamelContext();
		
		SimpleRegistry  registry = new SimpleRegistry ();
		
		MyReverseDataFormat reverse = new MyReverseDataFormat();
		registry.put("reverse", reverse);
		
		CaseDataFormat caseconvert = new CaseDataFormat();
		registry.put("caseconvert", caseconvert);
		
		ctx.setRegistry(registry);
		ctx.addRoutes(this);
		ctx.start();
		
		ProducerTemplate producer = ctx.createProducerTemplate();
		String result = (String) producer.requestBody("direct:start", "1234567890dhlee");	 	
		ctx.stop();
		 
		logger.info("Main result = " + result);
	}
	
	@Override
	public void configure() throws Exception {
		
		from("direct:start")
		.log("Flat Text : ${body}")
		.marshal().custom("reverse")
		.convertBodyTo(String.class)
	    .log("reverse : ${body}")
	    .setProperty("CASE").constant("UPPER")
	    .marshal().custom("caseconvert")
		.convertBodyTo(String.class)
	    .log("case : ${body}");
	}

}
