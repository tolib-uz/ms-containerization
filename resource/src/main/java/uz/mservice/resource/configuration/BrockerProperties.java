package uz.mservice.resource.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "broker")
public class BrockerProperties {
    private String queue;
    private String exchange;
    private String routingkey;

    public BrockerProperties() {
    }

    public BrockerProperties(String queue, String exchange, String routingkey) {
        this.queue = queue;
        this.exchange = exchange;
        this.routingkey = routingkey;
    }

    public String getQueue() {
        return queue;
    }

    public String getExchange() {
        return exchange;
    }

    public String getRoutingkey() {
        return routingkey;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public void setRoutingkey(String routingkey) {
        this.routingkey = routingkey;
    }
}
