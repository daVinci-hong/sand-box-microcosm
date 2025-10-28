package com.projectdavinci.common.services;

/**
 * A universal, abstract interface for publishing events to a messaging system.
 * This contract decouples producers from the underlying message broker implementation (e.g., Kafka, RabbitMQ).
 */
public interface MessagingService {
    /**
     * Publishes an event to a specified topic.
     * @param topic The target topic for the event.
     * @param event The event payload.
     * @param <T> The type of the event payload.
     */
    <T> void publishEvent(String topic, T event);
}