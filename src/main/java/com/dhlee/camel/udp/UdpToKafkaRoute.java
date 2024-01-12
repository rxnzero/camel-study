package com.dhlee.camel.udp;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class UdpToKafkaRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        // UDP endpoint to receive data
        from("netty:udp://0.0.0.0:9876")
            .log("Received UDP message: ${body}")
            
            // Process the message to convert String to JSON
            .process(new StringToJsonProcessor())
            
            // Send the JSON message to Kafka topic
            .to("kafka:your_kafka_topic?brokers=your_kafka_broker");
    }

    private static class StringToJsonProcessor implements Processor { 
        @Override
        public void process(Exchange exchange) throws Exception {
            // Get the original body (assumed to be a String)
            String originalBody = exchange.getIn().getBody(String.class);

            // Convert the String to a JSON structure
            String jsonBody = String.format("{\"message\": \"%s\"}", originalBody);

            // Set the new JSON body
            exchange.getIn().setBody(jsonBody);
        }
    }
}
