package com.projectdavinci.beaconservice;

import com.projectdavinci.common.events.BeaconEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BeaconEventConsumerTest {

    @Mock
    private BeaconService beaconService; // 假設有一個處理業務邏輯的 Service

    @InjectMocks
    private BeaconEventConsumer beaconEventConsumer;

    @Test
    void whenEventIsReceived_thenServiceIsCalled() {
        // 1. 準備 (Arrange)
        BeaconEvent testEvent = new BeaconEvent(
                UUID.randomUUID().toString(),
                "test-user",
                Instant.now()
        );

        // 2. 行動 (Act)
        beaconEventConsumer.consumeBeaconEvent(testEvent);

        // 3. 斷言 (Assert)
        // 驗證 beaconService 的 processEvent 方法，是否被以 testEvent 為參數，準確地調用了一次
        verify(beaconService, times(1)).processEvent(testEvent);
    }
}