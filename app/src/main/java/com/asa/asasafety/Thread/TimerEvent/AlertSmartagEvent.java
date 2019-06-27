package com.asa.asasafety.Thread.TimerEvent;

import com.asa.asasafety.MokoSupportAdaptor.MokoSupportManager;
import com.asa.asasafety.ObjectManager.SafetyObjectManager;
import com.moko.support.entity.DeviceInfo;

public class AlertSmartagEvent extends TimerEvent {
    private MokoSupportManager mokoSupportManager;
    DeviceInfo deviceToDelete;

    public AlertSmartagEvent(MokoSupportManager mokoSupportManager) {
        super(1, true);
        this.mokoSupportManager = mokoSupportManager;

        deviceToDelete = new DeviceInfo();
        deviceToDelete.mac = "D1:C4:42:12:48:90";
    }

    protected void event() {
        SafetyObjectManager.minorRemainLightingTimeByOne();
        SafetyObjectManager.removeOldSmartagRecords();
        mokoSupportManager.tryToTakeSmartagToConnect();
        mokoSupportManager.updateUI();

        //Call this function to close the specific smartag
//        tmpClose();
    }

    public void tmpClose() {
        if (mokoSupportManager.getDeviceInfoList()==null) {
            return;
        }
        if (mokoSupportManager.getDeviceInfoList().contains(deviceToDelete)) {
            mokoSupportManager.setCloseRequest();
            mokoSupportManager.simpleConnect(deviceToDelete);
            deviceToDelete.mac = "N/A";
        }
    }
}
