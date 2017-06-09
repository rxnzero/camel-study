package com.dhlee.camel.jdbc;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class RowProcessor implements Processor {

	public void process(Exchange exchange) throws Exception {
		Map<String, Object> row = exchange.getIn().getBody(Map.class);
		
		User user = new User();
		
		System.out.println("Processing " + row);
		user.setId((String)row.get("id"));
		user.setName((String)row.get("name"));
		
		exchange.getOut().setBody(user);
	}

}
