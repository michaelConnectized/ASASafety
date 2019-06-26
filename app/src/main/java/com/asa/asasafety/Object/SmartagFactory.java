package com.asa.asasafety.Object;

import com.moko.support.entity.DeviceInfo;

public class SmartagFactory {
    public static Smartag getSmartag(DeviceInfo deviceInfo) {
        return (Smartag)deviceInfo;
    }

    public static VirtualSmartag getVirtualSmartag(DeviceInfo deviceInfo) {
        return (VirtualSmartag)deviceInfo;
    }
}
