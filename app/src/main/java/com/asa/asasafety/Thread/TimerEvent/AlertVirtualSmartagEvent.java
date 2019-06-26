package com.asa.asasafety.Thread.TimerEvent;

import com.asa.asasafety.Model.ApiConnectionAdaptor;
import com.asa.asasafety.MokoSupportAdaptor.MokoSupportManager;
import com.asa.asasafety.ObjectManager.SafetyObjectManager;

public class AlertVirtualSmartagEvent extends TimerEvent {
    private ApiConnectionAdaptor apiConnectionAdaptor;

    public AlertVirtualSmartagEvent(ApiConnectionAdaptor apiConnectionAdaptor) {
        super(1, true);
        this.apiConnectionAdaptor = apiConnectionAdaptor;
    }

    protected void event() {
        SafetyObjectManager.minorRemainNextAlertTimeByOne();
        SafetyObjectManager.removeOldVirtualSmartagRecords();
        ApiConnectionAdaptor.sendAlertsToServer();
    }
}
