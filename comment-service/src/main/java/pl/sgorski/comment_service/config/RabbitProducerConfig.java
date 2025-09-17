package pl.sgorski.comment_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.sgorski.comment_service.config.properties.RabbitCommentExchangeProperties;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class RabbitProducerConfig {

    private final RabbitCommentExchangeProperties rabbitProperties;

    @Bean
    public TopicExchange topicExchange() { return new TopicExchange(rabbitProperties.exchangeName(), true, false); }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() { return new Jackson2JsonMessageConverter(); }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory cf) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(cf);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        rabbitTemplate.setReturnsCallback(r -> log.warn("Message returned from exchange: {}, routingKey: {}", r.getExchange(), r.getRoutingKey()));
        return rabbitTemplate;
    }
}
