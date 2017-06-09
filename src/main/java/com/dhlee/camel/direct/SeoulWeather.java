package com.dhlee.camel.direct;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.w3c.dom.Document;

public class SeoulWeather extends RouteBuilder {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		SeoulWeather weather = new SeoulWeather();
		weather.printWeather();
	}
	
	public void printWeather() throws Exception {
		System.setProperty("org.apache.camel.jmx.disabled", "true");
		
		DefaultCamelContext ctx = new DefaultCamelContext();
		SimpleRegistry registry = new SimpleRegistry();
		registry.put("weatherExtractor", new WeatherExtractor());
		
		ctx.setRegistry(registry);
		ctx.addRoutes(this);
		ctx.start();
		
		ProducerTemplate producer = ctx.createProducerTemplate();
		producer.sendBody("direct:start", "");
		
		ctx.stop();
		
	}

	@Override
	public void configure() throws Exception {
		from("direct:start").
		setHeader(Exchange.HTTP_QUERY, simple("httpClient.SocketTimeout=1000")).
		to("http4://www.kma.go.kr/weather/forecast/mid-term-xml.jsp?stnId=109").

// xpath & xmljson		
		convertBodyTo(java.lang.String.class).
		log("original xml : ${body}").
		setBody(xpath("//wid/body/location[@city='11B10101']")).    
		log("xpath : ${body}").
		marshal().xmljson().
		log("XML2JSON : ").
		convertBodyTo(java.lang.String.class).
		to("stream:out");
		
//  Bean transform		
//		convertBodyTo(Document.class).
//		to("bean:weatherExtractor?method=print");
	}
}
