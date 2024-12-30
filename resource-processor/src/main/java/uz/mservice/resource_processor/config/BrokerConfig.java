package uz.mservice.resource_processor.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class BrokerConfig {

    private final ProcessorBrokerProperties brokerProperties;

    public BrokerConfig(ProcessorBrokerProperties brokerProperties) {
        this.brokerProperties = brokerProperties;
    }

    @Bean
    public Queue queue() {
        return new Queue(brokerProperties.getQueue(), false);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(brokerProperties.getExchange());
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(brokerProperties.getRoutingkey());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
