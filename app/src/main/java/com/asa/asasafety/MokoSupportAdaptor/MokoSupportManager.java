package com.asa.asasafety.MokoSupportAdaptor;

import android.app.Activity;

import com.moko.support.MokoSupport;
import com.moko.support.entity.DeviceInfo;

import java.util.ArrayList;
import java.util.List;

public class MokoSupportManager {
    private Activity activity;
    private MokoSupportAdaptor mokoSupportAdaptor;

    private List<DeviceInfo> deviceQueryQueue;
    private DeviceInfo sendingDevice;

    public MokoSupportManager(Activity activity) {
        this.activity = activity;
        init();
    }

    private void init() {
        mokoSupportAdaptor = new MokoSupportAdaptor(activity);
        mokoSupportAdaptor.startService();
    }

    public boolean startScan() {
        if (!mokoSupportAdaptor.isBluetoothOn()) {
            mokoSupportAdaptor.requestTurnOnBluetooth(activity);
        } else {
            if (mokoSupportAdaptor.startScan()) {
                return true;
            }
        }
        return false;
    }

    public void stopScan() {
        mokoSupportAdaptor.stopScan();
    }

    public void setLedRequest() {
        mokoSupportAdaptor.initRequest();
        mokoSupportAdaptor.setLedRequest(30);
    }

    public void sendAllRequestsToAllDevices() {
        deviceQueryQueue = new ArrayList<>(mokoSupportAdaptor.getDeviceInfoList());
        mokoSupportAdaptor.setOnStatusChangedListener(new MokoSupportAdaptor.onStatusChangedListener() {
            @Override
            public void onStatusChanged(MokoSupportAdaptor.Status status) {
                switch (status) {
                    case CONNECT_SUCCESS:  break;
                    case READY_TO_SEND:
                        deviceQueryQueue.remove(sendingDevice);
                        mokoSupportAdaptor.sendRequest();
                        mokoSupportAdaptor.disconnectDevice();
                        break;
                    case DISCONNECTED:
                        if (deviceQueryQueue.size()>0) {
                            sendingDevice = deviceQueryQueue.get(0);
                            mokoSupportAdaptor.connectDevice(sendingDevice);
                        }
                        break;
                }
            }
        });

        if (deviceQueryQueue.size()>0) {
            sendingDevice = deviceQueryQueue.get(0);
            mokoSupportAdaptor.connectDevice(sendingDevice);
        }
    }

    public List<DeviceInfo> getDeviceInfoList() {
        return mokoSupportAdaptor.getDeviceInfoList();
    }
}
