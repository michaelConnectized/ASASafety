package com.asa.safety.safety.event;

import android.util.Log;

import com.asa.safety.safety.mokoSupportAdaptor.MokoSupportManager;
import com.asa.safety.safety.objectManager.SafetyObjectManager;
import com.asa.safety.utils.thread.event.TimerEvent;

import java.util.Date;

public class AlertSmartagEvent extends TimerEvent {
    protected String eventName = "AlertSmartagEvent";
    private long timeout = 40 * 1000; //60s

    private MokoSupportManager mokoSupportManager;

    public AlertSmartagEvent(MokoSupportManager mokoSupportManager) {
        super(1, true);
        this.mokoSupportManager = mokoSupportManager;
    }

    protected void event() {
        if (isTimeout()) {
            mokoSupportManager.isTryingConnection = false;
        }
        SafetyObjectManager.removeOldSmartagRecords();
        mokoSupportManager.tryToTakeSmartagToConnect();
    }

    private boolean isTimeout() {
        long currentTime = new Date().getTime();
        return (currentTime - mokoSupportManager.lastConnectionTime) >= timeout;
    }
}
