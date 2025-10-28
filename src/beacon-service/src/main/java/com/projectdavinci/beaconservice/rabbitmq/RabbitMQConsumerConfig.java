package com.projectdavinci.beaconservice.rabbitmq;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("rabbitmq")
@Configuration
public class RabbitMQConsumerConfig {

    // =================================================================
    // == 【【【 唯一的、核心的修正：為“消費者”，指派“智慧的翻譯官” 】】】 ==
    // =================================================================
    // 我們，將，鑄造，一個“智慧的”監聽器工廠。
    // 這個工廠，所創造的，所有“監聽器”，都，將，被，強制性地，配備，我們唯一的、正確的“JSON 翻譯官”。
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }
}