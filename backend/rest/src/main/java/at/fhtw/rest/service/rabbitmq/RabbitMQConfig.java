package at.fhtw.rest.service.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "documentQueue";
    public static final String EXCHANGE_NAME = "documentExchange";
    public static final String ROUTING_KEY = "document.routingKey";

    @Bean
    public Queue documentQueue() {
        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    public TopicExchange documentExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Binding binding(Queue documentQueue, TopicExchange documentExchange) {
        return BindingBuilder.bind(documentQueue).to(documentExchange).with(ROUTING_KEY);
    }
}
