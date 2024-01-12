package com.dhlee.camel.udp;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

public class MainApp {
    public static void main(String[] args) throws Exception {
        // Create Camel context
        CamelContext camelContext = new DefaultCamelContext();

        // Add UdpToKafkaRoute and ImpalaCamelRoute to the context
        camelContext.addRoutes(new UdpToKafkaRoute());
        camelContext.addRoutes(new ImpalaCamelRoute());

        // Start the Camel context
        camelContext.start();

        // Keep the application running for a while to receive UDP messages and process them

        // Stop the Camel context
        camelContext.stop();
    }
}
