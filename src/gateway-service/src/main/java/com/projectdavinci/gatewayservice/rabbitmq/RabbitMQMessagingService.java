package com.projectdavinci.gatewayservice.rabbitmq;

import com.projectdavinci.common.services.MessagingService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("rabbitmq") // 僅在“兔子模式”下激活
public class RabbitMQMessagingService implements MessagingService {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMQMessagingService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public <T> void publishEvent(String topic, T event) {
        // 我們，將，使用，一個，明確的、無歧義的“路由密鑰”。
        rabbitTemplate.convertAndSend("exchange.beacon.events", "routingkey.beacon.created", event);
    }
}