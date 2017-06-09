package com.dhlee.camel.netty;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.netty.ChannelHandlerFactories;
import org.apache.camel.component.netty.ChannelHandlerFactory;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TcpRouter extends RouteBuilder {
	
	private final Logger logger = LoggerFactory.getLogger(TcpRouter.class);
	
	public static void main(String[] args) throws Exception {
		TcpRouter loopRouter = new TcpRouter();		
		loopRouter.run();
		
	}
	private void run() throws Exception {
		System.setProperty("org.apache.camel.jmx.disabled", "true");
		
		DefaultCamelContext ctx = new DefaultCamelContext();
		
		SimpleRegistry  registry = new SimpleRegistry ();
		// newLengthFieldBasedFrameDecoder(
		//	int maxFrameLength, 
		//	int lengthFieldOffset, 
		//	int lengthFieldLength, 
		//	int lengthAdjustment, 
		//	int initialBytesToStrip)
		ChannelHandlerFactory lengthDecoder = ChannelHandlerFactories.newLengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4);
		 
		StringDecoder stringDecoder = new StringDecoder();
		registry.put("length-decoder", lengthDecoder);
		registry.put("string-decoder", stringDecoder);
		 
		LengthFieldPrepender lengthEncoder = new LengthFieldPrepender(4);
		StringEncoder stringEncoder = new StringEncoder();
		registry.put("length-encoder", lengthEncoder);
		registry.put("string-encoder", stringEncoder);
		 
		List<ChannelHandler> decoders = new ArrayList<ChannelHandler>();
		decoders.add(lengthDecoder);
		decoders.add(stringDecoder);
		 
		List<ChannelHandler> encoders = new ArrayList<ChannelHandler>();
		encoders.add(lengthEncoder);
		encoders.add(stringEncoder);
		 
		registry.put("encoders", encoders);
		registry.put("decoders", decoders);
		
		FixedLengthServerPipelineFactory tcpFixedLengthServer = new FixedLengthServerPipelineFactory(null);
		registry.put("tcpFixedLengthServer", tcpFixedLengthServer);
		
		FixedLengthClientPipelineFactory tcpFixedLengthClient = new FixedLengthClientPipelineFactory(null);
		registry.put("tcpFixedLengthClient", tcpFixedLengthClient);
		
		
		ctx.setRegistry(registry);
		
		ctx.addRoutes(this);
		ctx.start();
		
		ProducerTemplate producer = ctx.createProducerTemplate();
		String result = (String) producer.requestBody("direct:multiple-codec", "Hello Netty!");	 	
		
		logger.info("Main result = " + result);
		
		Thread.sleep(10 * 1000);
		
		ctx.stop();
		
		
	}
	
	@Override
	public void configure() throws Exception {		
		// Sender
		from("direct:multiple-codec")
		.routeId("sender-route")
		.log("send message : ${body}")
		.to("netty:tcp://localhost:5504?encoders=#encoders&decoders=#decoders&sync=true");
        
		// Router 
		from("netty:tcp://localhost:5504?serverPipelineFactory=#tcpFixedLengthServer&sync=true").routeId("route-route")
		.process(new Processor() { 
			public void process(Exchange exchange) throws Exception { 
				 exchange.getOut().setBody("change message contents");
				// Do something or not
			} 
			 }) 
		.log("router receive message : ${body}")
		.to("netty:tcp://localhost:5505?clientPipelineFactory=#tcpFixedLengthClient&sync=true")
//		.process(new Processor() { 
//			public void process(Exchange exchange) throws Exception { 
//				// Do something or not 
//			} 
//		 })
		.log("router return message : ${body}");
			
		//Receiver 
		from("netty:tcp://localhost:5505?decoders=#length-decoder,#string-decoder&encoders=#encoders&sync=true")
		.routeId("receiver-route")
		.log("receive message : ${body}")
		.setBody(simple("${body}, I'm server Netty"))
		.log("response message : ${body}");
		
		
	}

}
