package com.asa.asasafety.Object;

import com.moko.support.entity.DeviceInfo;

public class VirtualSmartag extends DeviceInfo {
    private int remainNextAlertTime = 5 * 60;

    public void minorRemainNextAlertTime() {
        remainNextAlertTime--;
    }

    public int getRemainNextAlertTime() {
        return remainNextAlertTime;
    }

    @Override
    public boolean equals(Object object) {
        boolean isSame = false;
        if (object instanceof VirtualSmartag) {
            isSame = this.mac.equals(((VirtualSmartag) object).mac);
        }
        return isSame;
    }
}
