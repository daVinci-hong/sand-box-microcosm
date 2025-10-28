package com.projectdavinci.beaconservice;

import com.projectdavinci.common.events.BeaconEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
@ActiveProfiles("kafka")
class KafkaIntegrationTest {

    // 1. 聲明一個由 Testcontainers 管理的 Kafka 容器
    @Container
    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

    // 2. 動態地，將容器的地址，注入到 Spring 的配置中
    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private KafkaTemplate<String, BeaconEvent> kafkaTemplate;

    // 3. 使用 @MockBean，替換掉真實的 Service，以便我們可以驗證其是否被調用
    @MockBean
    private BeaconService beaconService;

    @Test
    void whenEventIsSent_thenItShouldBeConsumed() {
        // Arrange: 準備一個測試事件
        BeaconEvent testEvent = new BeaconEvent(
                UUID.randomUUID().toString(),
                "integration-test",
                Instant.now()
        );

        // Act: 使用注入的 KafkaTemplate，向由 Testcontainers 啟動的真實 Kafka 發送事件
        kafkaTemplate.send("topic.beacon.events", testEvent);

        // Assert: 使用 Awaitility，在最多 10 秒內，輪詢驗證
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            // 驗證我們 mock 的 beaconService 的 processEvent 方法，是否被以 testEvent 為參數，準確地調用了一次
            verify(beaconService).processEvent(testEvent);
        });
    }
}