package com.dhlee.camel.netty;

import org.apache.camel.component.netty.NettyConsumer;
import org.apache.camel.component.netty.ServerPipelineFactory;
import org.apache.camel.component.netty.handlers.ServerChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.logging.InternalLogLevel;

public class FixedLengthServerPipelineFactory extends ServerPipelineFactory {
	
	private NettyConsumer consumer;
	
	private final LengthFieldPrepender LENGTH_FIELD_PREPENDER = new LengthFieldPrepender(4);
	
	public FixedLengthServerPipelineFactory(NettyConsumer consumer) {
		this.consumer = consumer;
	}
	
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline(); 
		pipeline.addLast("hexdump", new LoggingHandler(FixedLengthServerPipelineFactory.class, InternalLogLevel.INFO, true));
		pipeline.addLast("frameEncoder", LENGTH_FIELD_PREPENDER);
		pipeline.addLast("stringEncoder", new StringEncoder());
		
		pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(8192, 0, 4, 0, 4));
		pipeline.addLast("stringDncoder", new StringDecoder());
		
		pipeline.addLast("handler", new ServerChannelHandler(consumer));
		return pipeline; 

	}

	@Override
	public ServerPipelineFactory createPipelineFactory(NettyConsumer consumer) {
		return new FixedLengthServerPipelineFactory(consumer);
	}

}
