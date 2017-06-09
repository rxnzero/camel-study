package com.dhlee.camel.netty;

import org.apache.camel.component.netty.ClientPipelineFactory;
import org.apache.camel.component.netty.NettyProducer;
import org.apache.camel.component.netty.handlers.ClientChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.logging.InternalLogLevel;

public class FixedLengthClientPipelineFactory extends ClientPipelineFactory {
	
	private NettyProducer producer;
	
	private final LengthFieldPrepender LENGTH_FIELD_PREPENDER = new LengthFieldPrepender(4);
	
	public FixedLengthClientPipelineFactory(NettyProducer producer) {
		this.producer = producer;
	}
	
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline(); 
		pipeline.addLast("hexdump", new LoggingHandler(FixedLengthClientPipelineFactory.class, InternalLogLevel.INFO, true));
		pipeline.addLast("frameEncoder", LENGTH_FIELD_PREPENDER);
		pipeline.addLast("stringEncoder", new StringEncoder());
		
		pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(8192, 0, 4, 0, 4));
		pipeline.addLast("stringDncoder", new StringDecoder());
		
		pipeline.addLast("handler", new ClientChannelHandler(producer));
		return pipeline; 

	}

	@Override
	public ClientPipelineFactory createPipelineFactory(NettyProducer producer) {
		return new FixedLengthClientPipelineFactory(producer);
	}

}
