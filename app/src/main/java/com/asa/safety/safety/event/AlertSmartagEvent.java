package com.asa.safety.safety.event;

import android.util.Log;

import com.asa.safety.safety.mokoSupportAdaptor.MokoSupportManager;
import com.asa.safety.safety.objectManager.SafetyObjectManager;
import com.asa.safety.utils.thread.event.TimerEvent;

import java.util.Date;

public class AlertSmartagEvent extends TimerEvent {
    protected String eventName = "AlertSmartagEvent";
    private long timeout = 60 * 1000; //60s

    private MokoSupportManager mokoSupportManager;

    public AlertSmartagEvent(MokoSupportManager mokoSupportManager) {
        super(1, true);
        this.mokoSupportManager = mokoSupportManager;
    }

    protected void event() {
        if (isTimeout()) {
            mokoSupportManager.isTryingConnection = false;
        }
        SafetyObjectManager.minorRemainLightingTimeByOne();
        SafetyObjectManager.removeOldSmartagRecords();
        mokoSupportManager.tryToTakeSmartagToConnect();
        mokoSupportManager.updateUI();
    }

    private boolean isTimeout() {
        long currentTime = new Date().getTime();
        Log.e(eventName, ":"+(currentTime - mokoSupportManager.lastConnectionTime));
        return (currentTime - mokoSupportManager.lastConnectionTime) >= timeout;
    }
}
