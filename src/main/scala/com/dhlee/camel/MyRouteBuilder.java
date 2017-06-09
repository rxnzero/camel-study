package com.dhlee.camel;

import org.apache.camel.scala.dsl.builder.RouteBuilder;

class MyRouteBuilder extends RouteBuilder {
	  "direct:a" --> "mock:a"
	  "direct:b" to "mock:b"      
}

