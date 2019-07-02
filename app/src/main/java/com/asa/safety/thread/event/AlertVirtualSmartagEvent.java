package com.asa.safety.thread.event;

import com.asa.safety.model.SafetyApiConnectionAdaptor;
import com.asa.safety.objectManager.SafetyObjectManager;
import com.asa.utils.thread.event.TimerEvent;

public class AlertVirtualSmartagEvent extends TimerEvent {
    protected String eventName = "AlertVirtualSmartagEvent";

    private SafetyApiConnectionAdaptor safetyApiConnectionAdaptor;
    private String localMacAddress;

    public AlertVirtualSmartagEvent(SafetyApiConnectionAdaptor safetyApiConnectionAdaptor, String localMacAddress) {
        super(1, true);
        this.safetyApiConnectionAdaptor = safetyApiConnectionAdaptor;
        this.localMacAddress = localMacAddress;
    }

    protected void event() {
        SafetyObjectManager.minorRemainNextAlertTimeByOne();
        SafetyObjectManager.removeOldVirtualSmartagRecords();
        safetyApiConnectionAdaptor.sendAlertsToServer(localMacAddress);
    }
}
