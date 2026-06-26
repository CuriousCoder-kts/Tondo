package com.tondo.infrastructure.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
@ConditionalOnProperty(name = "tondo.mq.notification-enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMqConfig {

    @Bean
    public Jackson2JsonMessageConverter rabbitMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter rabbitMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(rabbitMessageConverter);
        return template;
    }

    @Bean
    public DirectExchange notificationExchange(RabbitMqProperties props) {
        return new DirectExchange(props.getNotificationExchange(), true, false);
    }

    @Bean
    public Queue notificationDlq(RabbitMqProperties props) {
        return QueueBuilder.durable(props.getNotificationDlq()).build();
    }

    @Bean
    public Queue notificationQueue(RabbitMqProperties props) {
        return QueueBuilder.durable(props.getNotificationQueue())
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", props.getNotificationDlq())
                .build();
    }

    @Bean
    public Binding notificationBinding(RabbitMqProperties props,
                                         Queue notificationQueue,
                                         DirectExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue)
                .to(notificationExchange)
                .with(props.getNotificationRoutingKey());
    }
}
