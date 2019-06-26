package com.asa.asasafety.Thread.TimerEvent;

import com.asa.asasafety.MokoSupportAdaptor.MokoSupportManager;
import com.asa.asasafety.ObjectManager.SafetyObjectManager;

public class AlertSmartagEvent extends TimerEvent {
    private MokoSupportManager mokoSupportManager;

    public AlertSmartagEvent(MokoSupportManager mokoSupportManager) {
        super(1, true);
        this.mokoSupportManager = mokoSupportManager;
    }

    protected void event() {
        SafetyObjectManager.minorRemainLightingTimeByOne();
        SafetyObjectManager.removeOldSmartagRecords();
        mokoSupportManager.tryToTakeSmartagToConnect();
    }
}
