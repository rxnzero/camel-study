package com.dhlee.camel.udp;

import org.apache.camel.builder.RouteBuilder;

public class ImpalaCamelRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        // Define the data source URI for Impala
        String dataSourceUri = "jdbc:impala://your_impala_host:21050/your_database";

        // Camel route to receive JSON data from Kafka and save it to Impala
        from("kafka:your_kafka_topic?brokers=your_kafka_broker")
            .unmarshal().json()
            .log("Received JSON message from Kafka: ${body}")
            .setBody(simple("INSERT INTO your_table (column1, column2) VALUES (:?value1, :?value2)"))
            .toF("sql:%s?dataSource=%s", dataSourceUri, dataSourceUri)
            .log("Data saved to Impala: ${body}");
    }
}

