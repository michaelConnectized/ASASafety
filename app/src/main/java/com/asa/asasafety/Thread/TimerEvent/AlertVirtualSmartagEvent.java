package com.asa.asasafety.Thread.TimerEvent;

import com.asa.asasafety.Model.ApiConnectionAdaptor;
import com.asa.asasafety.MokoSupportAdaptor.MokoSupportManager;
import com.asa.asasafety.ObjectManager.SafetyObjectManager;

public class AlertVirtualSmartagEvent extends TimerEvent {
    private ApiConnectionAdaptor apiConnectionAdaptor;
    private String localMacAddress;

    public AlertVirtualSmartagEvent(ApiConnectionAdaptor apiConnectionAdaptor, String localMacAddress) {
        super(1, true);
        this.apiConnectionAdaptor = apiConnectionAdaptor;
        this.localMacAddress = localMacAddress;
    }

    protected void event() {
        SafetyObjectManager.minorRemainNextAlertTimeByOne();
        SafetyObjectManager.removeOldVirtualSmartagRecords();
        apiConnectionAdaptor.sendAlertsToServer(localMacAddress);
    }
}
