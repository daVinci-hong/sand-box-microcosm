package com.projectdavinci.beaconservice;

import com.projectdavinci.common.events.BeaconEvent;

public interface BeaconService {
    void processEvent(BeaconEvent event);
}