package com.projectdavinci.beaconservice;

import com.projectdavinci.common.events.BeaconEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BeaconServiceImpl implements BeaconService {

    private static final Logger log = LoggerFactory.getLogger(BeaconServiceImpl.class);

    @Override
    public void processEvent(BeaconEvent event) {
        log.info("==> [Beacon Service Logic] Processing BeaconEvent <==");
        log.info("  - Event ID: {}", event.eventId());
        log.info("  - Triggered By: {}", event.triggeredBy());
        log.info("  - Timestamp: {}", event.timestamp());
        log.info("Event processed successfully by service layer.");
    }
}