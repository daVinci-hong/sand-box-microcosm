package com.projectdavinci.common.events;

public record BeaconEvent(
    String eventId,
    String triggeredBy,
    java.time.Instant timestamp
) {}