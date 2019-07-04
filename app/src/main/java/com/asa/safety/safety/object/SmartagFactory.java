package com.asa.safety.safety.object;

import com.moko.support.entity.DeviceInfo;

public class SmartagFactory {
    public static Smartag getSmartag(DeviceInfo deviceInfo) {
        Smartag smartag = new Smartag();
        smartag.mac = deviceInfo.mac;
        smartag.name = deviceInfo.name;
        smartag.rssi = deviceInfo.rssi;
        smartag.scanRecord = deviceInfo.scanRecord;
        smartag.issueMessage = deviceInfo.issueMessage;
        return smartag;
    }

    public static VirtualSmartag getVirtualSmartag(DeviceInfo deviceInfo) {
        VirtualSmartag smartag = new VirtualSmartag();
        smartag.mac = deviceInfo.mac;
        smartag.name = deviceInfo.name;
        smartag.rssi = deviceInfo.rssi;
        smartag.scanRecord = deviceInfo.scanRecord;
        smartag.issueMessage = deviceInfo.issueMessage;
        return smartag;
    }
}
