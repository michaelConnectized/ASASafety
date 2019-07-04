package com.asa.safety.safety.object;

import com.moko.support.entity.DeviceInfo;

public class VirtualSmartag extends DeviceInfo {
    private int remainNextAlertTime = 5 * 60;
    private boolean isSent = false;

    public void minorRemainNextAlertTime() {
        if (isSent)
            remainNextAlertTime--;
    }

    public int getRemainNextAlertTime() {
        return remainNextAlertTime;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
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
