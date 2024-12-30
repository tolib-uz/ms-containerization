package uz.mservice.resource_processor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "broker")
public class ProcessorBrokerProperties {

    private String queue;
    private  String exchange;
    private  String routingkey;
    public ProcessorBrokerProperties() {
    }

    public ProcessorBrokerProperties(String queue, String exchange, String routingkey) {
        this.queue = queue;
        this.exchange = exchange;
        this.routingkey = routingkey;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRoutingkey() {
        return routingkey;
    }

    public void setRoutingkey(String routingkey) {
        this.routingkey = routingkey;
    }
}
