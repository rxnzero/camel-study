package com.dhlee.camel.pattern;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.processor.DefaultExchangeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class DynamicRouter extends RouteBuilder {
	
	private final Logger logger = LoggerFactory.getLogger(DynamicRouter.class);
	
	public static void main(String[] args) throws Exception {
		DynamicRouter loopRouter = new DynamicRouter();		
		loopRouter.run();
		
	}
	private void run() throws Exception {
		System.setProperty("org.apache.camel.jmx.disabled", "true");
		
		DefaultCamelContext ctx = new DefaultCamelContext();
		
		ctx.addRoutes(this);
		ctx.start();
		
		ProducerTemplate producer = ctx.createProducerTemplate();
		String result = (String) producer.requestBody("direct:start", "Dynamic route with header value");		
		
		ctx.stop();
		
		logger.info("Main result = " + result);
	}
	
	@Override
	public void configure() throws Exception {
		errorHandler(
		        deadLetterChannel("direct:deadLetter")
		            .maximumRedeliveries(2)
		            .retryAttemptedLogLevel(LoggingLevel.WARN)
		            );

		from("direct:start")
		.log("request body : ${body}")
		.setHeader("to-path").constant("stream:out")
		.toD("${header.to-path}")
		.setBody(simple("${body} - ${header.to-path}"))
		.setHeader("to-path", constant("log1:stream"))
		.toD("${header.to-path}")
		.setBody(simple("${body} - ${header.to-path}"));
		
		from("direct:deadLetter").id("deadLetter")
		    .routeId("deadLetter")
		    .errorHandler(defaultErrorHandler().disableRedelivery())
		    .process(new Processor() {
		        public void process(Exchange msg) {
		            String MsgHist = org.apache.camel.util.MessageHelper.dumpMessageHistoryStacktrace(msg, new DefaultExchangeFormatter(), false);
		            logger.error(MsgHist, msg);
		            msg.getOut().setBody(msg.getIn().getBody(String.class), String.class);
		            msg.getOut().setHeaders(msg.getIn().getHeaders());
		            msg.getOut().setHeader("MessageHistory",MsgHist);
		        }
		    })
		    .log(LoggingLevel.WARN,"${exception.stacktrace}")
		    .setHeader("ErrorMessage",simple("${exception}",String.class))
		    .setHeader("ErrorStacktrace",simple("${exception.stacktrace}",String.class))
		    .to("stream:out");  // activemqWithoutTransactions:errors
	}

}
