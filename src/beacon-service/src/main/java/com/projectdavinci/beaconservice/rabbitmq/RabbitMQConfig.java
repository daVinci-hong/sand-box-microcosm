package com.projectdavinci.beaconservice.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("rabbitmq")
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "exchange.beacon.events";
    public static final String QUEUE_NAME = "queue.beacon.events";
    public static final String ROUTING_KEY = "routingkey.beacon.#";

    @Bean
    public TopicExchange beaconEventsExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean("beaconEventsQueue") // 我們，將此 Bean，命名為 “beaconEventsQueue”
    public Queue beaconEventsQueue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Binding binding(Queue beaconEventsQueue, TopicExchange beaconEventsExchange) {
        return BindingBuilder.bind(beaconEventsQueue).to(beaconEventsExchange).with(ROUTING_KEY);
    }
}