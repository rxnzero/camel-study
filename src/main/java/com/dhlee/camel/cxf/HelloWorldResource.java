package com.dhlee.camel.cxf;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class HelloWorldResource implements HelloWorld {
	
	public HelloWorldResource() {
		System.out.println("Init....");;
	}
	@Override
	public Response greet() {
		return Response.status(Status.OK).
                entity("Hi There!!").
                   build();

	}

	@Override
	public Response sayHello(String input) {
		Hello hello = new Hello();
	       hello.setHello("Hello");
	       hello.setName("Default User");
	 
	        if(input != null)
	           hello.setName(input);
	 
	       return Response.
	                 status(Status.OK).
	                   entity(hello).
	                     build();
	}
	
	class Hello {
		   private String hello;
		   private String name;
		 
		   public String getHello() { return hello; }
		   public void setHello(String hello) { this.hello = hello; }
		 
		   public String getName() { return name; }
		   public void setName(String name) { this.name = name; }
	}

}
