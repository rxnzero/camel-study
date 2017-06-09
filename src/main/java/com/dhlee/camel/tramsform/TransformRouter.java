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

public class TransformRouter extends RouteBuilder {
	
	private final Logger logger = LoggerFactory.getLogger(TransformRouter.class);
	
	public static void main(String[] args) throws Exception {
		TransformRouter router = new TransformRouter();		
		router.run();
		
	}
	private void run() throws Exception {
		System.setProperty("org.apache.camel.jmx.disabled", "true");
		
		DefaultCamelContext ctx = new DefaultCamelContext();
		
		SimpleRegistry  registry = new SimpleRegistry ();
		
		BindyFixedLengthDataFormat bindy = new BindyFixedLengthDataFormat(com.dhlee.camel.tramsform.FixedFormatUser.class);
		registry.put("bindy", bindy);
		
		GsonDataFormat gson = new GsonDataFormat(com.dhlee.camel.tramsform.FixedFormatUser.class);
		gson.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
		registry.put("gson", gson);
		
		ctx.setRegistry(registry);
		ctx.addRoutes(this);
		ctx.start();
		
		// Enable
//		ctx.getProperties().put("CamelJacksonEnableTypeConverter", "true");
//		ctx.getProperties().put("CamelJacksonTypeConverterToPojo", "true");
		
		ProducerTemplate producer = ctx.createProducerTemplate();
		String result = (String) producer.requestBody("direct:start", "00001 이동훈       donghoon^ lee ^");	 	
		ctx.stop();
		 
		logger.info("Main result = " + result);
	}
	
	@Override
	public void configure() throws Exception {
		
		Map<String, String> xmlJsonOptions = new HashMap<String, String>();
		xmlJsonOptions.put(org.apache.camel.model.dataformat.XmlJsonDataFormat.ENCODING, "UTF-8");
		xmlJsonOptions.put(org.apache.camel.model.dataformat.XmlJsonDataFormat.ROOT_NAME, "fixed-format-user");
		xmlJsonOptions.put(org.apache.camel.model.dataformat.XmlJsonDataFormat.SKIP_NAMESPACES, "true");
		xmlJsonOptions.put(org.apache.camel.model.dataformat.XmlJsonDataFormat.REMOVE_NAMESPACE_PREFIXES, "true");
		xmlJsonOptions.put(org.apache.camel.model.dataformat.XmlJsonDataFormat.EXPANDABLE_PROPERTIES, "d e");

		
		from("direct:start")
		.log("Flat Text : ${body}")
		.unmarshal("bindy")
	    .log("Object : ${body}")
	    
	    // case 1.  JSON make json with class name
//	    .marshal().json()
	    
	 // case 2.  GSON make json with simple
//	    .marshal("gson")
//	    .log("json : ${body}")
//	    .unmarshal().xmljson(xmlJsonOptions)
	    
		// case 3.  CASTER make xml with simple 
	    .marshal().castor()
	    .log("XML : ${body}")
	    .marshal().xmljson(xmlJsonOptions)

	    // case 4.  XMLBEANS make xml with simple 
//	    .marshal().xmlBeans()

	    .convertBodyTo(String.class)
	    .log("Response : ${body}");
		

	}

}
