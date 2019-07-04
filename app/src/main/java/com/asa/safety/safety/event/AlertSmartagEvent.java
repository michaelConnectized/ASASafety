package com.asa.safety.safety.event;

import com.asa.safety.safety.mokoSupportAdaptor.MokoSupportManager;
import com.asa.safety.safety.objectManager.SafetyObjectManager;
import com.asa.safety.utils.thread.event.TimerEvent;

public class AlertSmartagEvent extends TimerEvent {
    protected String eventName = "AlertSmartagEvent";

    private MokoSupportManager mokoSupportManager;

    public AlertSmartagEvent(MokoSupportManager mokoSupportManager) {
        super(1, true);
        this.mokoSupportManager = mokoSupportManager;
    }

    protected void event() {
        SafetyObjectManager.minorRemainLightingTimeByOne();
        SafetyObjectManager.removeOldSmartagRecords();
        mokoSupportManager.tryToTakeSmartagToConnect();
        mokoSupportManager.updateUI();
    }
}
