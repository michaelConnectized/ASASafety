package com.asa.attend.analyser;

import android.util.Log;

//
public class BeaconAnalyser {
    public final static int MINEW = 0;
    public final static int APPLEIBEACON = 1;
    public final static int HELMETV3 = 2;
    public final static int HELMETV2 = 3;
    public final static int HELMETV2_P2 = 5;
    public final static int HELMETV1 = 4;

    private String tag = "BeaconAnalyser";
    private minewDevice minew;
    private appleIbeaconDevice aIbeacon;
    private helmetV3Device hmV3Device;
    private helmetV2Device hmV2Device;
    private helmetV1Device hmV1Device;


    private byte[] oriData;
    private int deviceCode;

    public BeaconAnalyser(byte[] oriData) {
        this.oriData = oriData;
        deviceCode = -1;

        if (minewDeviceInfo()) {
            deviceCode = MINEW;
        } else if (helmetVersion3Info()) {
            deviceCode = HELMETV3;
        } else if (helmetVersion2Info()) {
            deviceCode = HELMETV2;
        } else if (helmetVersion1Info()) {
            deviceCode = HELMETV1;
        } else if (iBeaconDeviceInfo()) {
            deviceCode = APPLEIBEACON;
        }
    }

    public boolean minewDeviceInfo() {
        boolean isSuccess;
        try {
            minew = new minewDevice(oriData);
            isSuccess = minew.isMinew();
        } catch (Exception e) {
            Log.e(tag, e.toString());
            isSuccess = false;
        }
        return isSuccess;
    }

    public boolean iBeaconDeviceInfo() {
        boolean isSuccess;
        try {
            aIbeacon = new appleIbeaconDevice(oriData);
            isSuccess = aIbeacon.isAppleIbeacon();
        } catch (Exception e) {
            Log.e(tag, e.toString());
            isSuccess = false;
        }
        return isSuccess;
    }

    public boolean helmetVersion1Info() {
        boolean isSuccess;
        try {
            hmV1Device = new helmetV1Device(oriData);
            isSuccess = hmV1Device.isHelmetV1Device();
        } catch (Exception e) {
            Log.e(tag, e.toString());
            isSuccess = false;
        }
        return isSuccess;
    }

    public boolean helmetVersion2Info() {
        boolean isSuccess;
        try {
            hmV2Device = new helmetV2Device(oriData);
            isSuccess = hmV2Device.isHelmetV2Device();
            if (!isSuccess) {
                isSuccess = hmV2Device.isPackage2();
            }
        } catch (Exception e) {
            Log.e(tag, e.toString());
            isSuccess = false;
        }
        return isSuccess;
    }

    public boolean helmetVersion3Info() {
        boolean isSuccess;
        try {
            hmV3Device = new helmetV3Device(oriData);
            isSuccess = hmV3Device.isHelmetV3Device();
        } catch (Exception e) {
            Log.e(tag, e.toString());
            isSuccess = false;
        }
        return isSuccess;
    }

    public minewDevice getMinew() {
        return minew;
    }

    public appleIbeaconDevice getaIbeacon() {
        return aIbeacon;
    }

    public helmetV3Device getHmV3Device() {
        return hmV3Device;
    }

    public helmetV2Device getHmV2Device() {
        return hmV2Device;
    }

    public int getDeviceCode() {
        return deviceCode;
    }
}
