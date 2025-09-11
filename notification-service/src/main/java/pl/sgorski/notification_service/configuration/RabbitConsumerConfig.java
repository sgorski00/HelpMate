package pl.sgorski.notification_service.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.sgorski.notification_service.configuration.properties.RabbitCommentExchangeProperties;
import pl.sgorski.notification_service.configuration.properties.RabbitTicketExchangeProperties;

@Log4j2
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RabbitTicketExchangeProperties.class)
@EnableRabbit
public class RabbitConsumerConfig {

    private final RabbitTicketExchangeProperties rabbitTicketExchangeProperties;
    private final RabbitCommentExchangeProperties rabbitCommentExchangeProperties;

    @Bean
    public TopicExchange ticketExchange() {
        return new TopicExchange(rabbitTicketExchangeProperties.exchangeName(), true, false);
    }

    @Bean
    public TopicExchange commentExchange() {
        return new TopicExchange(rabbitCommentExchangeProperties.exchangeName(), true, false);
    }

    @Bean
    public TopicExchange ticketDlx() {
        return new TopicExchange(rabbitTicketExchangeProperties.dlx(), true, false);
    }

    @Bean
    public TopicExchange commentDlx() {
        return new TopicExchange(rabbitCommentExchangeProperties.dlx(), true, false);
    }

    @Bean(name = "ticketCreatedQueue")
    public Queue ticketCreatedQueue() {
        return QueueBuilder.durable(rabbitTicketExchangeProperties.createdQueue())
                .deadLetterExchange(rabbitTicketExchangeProperties.dlx())
                .deadLetterRoutingKey(rabbitTicketExchangeProperties.createdRoutingKey())
                .build();
    }

    @Bean
    public Binding ticketCreatedBinding() {
        return BindingBuilder
                .bind(ticketCreatedQueue())
                .to(ticketExchange())
                .with(rabbitTicketExchangeProperties.createdRoutingKey());
    }

    @Bean(name = "ticketAssignedQueue")
    public Queue ticketAssignedQueue() {
        return QueueBuilder.durable(rabbitTicketExchangeProperties.assignedQueue())
                .deadLetterExchange(rabbitTicketExchangeProperties.dlx())
                .deadLetterRoutingKey(rabbitTicketExchangeProperties.assignedRoutingKey())
                .build();
    }

    @Bean
    public Binding ticketAssignedBinding() {
        return BindingBuilder
                .bind(ticketAssignedQueue())
                .to(ticketExchange())
                .with(rabbitTicketExchangeProperties.assignedRoutingKey());
    }

    @Bean(name = "ticketsDlq")
    public Queue ticketsDlq() {
        return QueueBuilder.durable(rabbitTicketExchangeProperties.dlq())
                .build();
    }

    @Bean
    public Binding ticketsDlqBinding() {
        return BindingBuilder
                .bind(ticketsDlq())
                .to(ticketDlx())
                .with("#");
    }

    @Bean(name = "commentCreatedQueue")
    public Queue commentCreatedQueue() {
        return QueueBuilder.durable(rabbitCommentExchangeProperties.createdQueue())
                .deadLetterExchange(rabbitCommentExchangeProperties.dlx())
                .deadLetterRoutingKey(rabbitCommentExchangeProperties.createdRoutingKey())
                .build();
    }

    @Bean
    public Binding commentCreatedBinding() {
        return BindingBuilder
                .bind(commentCreatedQueue())
                .to(commentExchange())
                .with(rabbitCommentExchangeProperties.createdRoutingKey());
    }

    @Bean(name = "commentsDlq")
    public Queue commentsDlq() {
        return QueueBuilder.durable(rabbitCommentExchangeProperties.dlq())
                .build();
    }

    @Bean
    public Binding commentsDlqBinding() {
        return BindingBuilder
                .bind(commentsDlq())
                .to(commentDlx())
                .with("#");
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory cf) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, cf);
        factory.setErrorHandler(eh -> {
            String errMsg = String.format("Messageing error: %s", eh.getCause().getMessage() != null ? eh.getCause().getMessage() : eh.getMessage());
            log.error(errMsg);
        });
        return factory;
    }
}
