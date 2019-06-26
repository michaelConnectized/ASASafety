package com.asa.asasafety.Object;


import com.moko.support.entity.DeviceInfo;

public class Smartag extends DeviceInfo {
    private int remainConnectionTimes = 3;
    private int remainLightingEndTime = 30;

    public void minorRemainConnectionTimes() {
        remainConnectionTimes--;
    }

    public void minorRemainLightingEndTime() {
        remainLightingEndTime--;
    }

    public int getRemainConnectionTimes() {
        return remainConnectionTimes;
    }

    public int getRemainLightingEndTime() {
        return remainLightingEndTime;
    }

    @Override
    public boolean equals(Object object) {
        boolean isSame = false;
        if (object instanceof Smartag) {
            isSame = this.mac.equals(((Smartag) object).mac);
        }
        return isSame;
    }
}
