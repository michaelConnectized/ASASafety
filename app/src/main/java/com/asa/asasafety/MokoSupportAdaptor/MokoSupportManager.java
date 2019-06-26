package com.asa.asasafety.MokoSupportAdaptor;

import android.app.Activity;
import android.util.Log;

import com.asa.asasafety.Object.Smartag;
import com.asa.asasafety.Object.SmartagFactory;
import com.asa.asasafety.ObjectManager.SafetyObjectManager;
import com.moko.support.entity.DeviceInfo;

import java.util.ArrayList;
import java.util.List;

public class MokoSupportManager {
    private Activity activity;
    private MokoSupportAdaptor mokoSupportAdaptor;
    private List<Smartag> smartagQueue;
    private Smartag targetSmartag;

    private boolean isTryingConnection = false;
    private boolean isSent = false;

    public MokoSupportManager(Activity activity) {
        this.activity = activity;
        initNewQueue();
        initMokoSupportAdaptor();
        initMokoSupportListener();
    }

    private void initNewQueue() {
        smartagQueue = new ArrayList<>();
    }

    private void initMokoSupportAdaptor() {
        mokoSupportAdaptor = new MokoSupportAdaptor(activity);
        mokoSupportAdaptor.startService();
    }

    private void initMokoSupportListener() {
        initOnStatusChangedListener();
        initOnScanDeviceListener();
    }

    private void initOnStatusChangedListener() {
        mokoSupportAdaptor.setOnStatusChangedListener((status) -> {
            switch (status) {
                //case CONNECT_SUCCESS: break;
                case READY_TO_SEND: readyToSendRequestEvent(); break;
                case DISCONNECTED: disconnectDeviceEvent(); break;
            }
        });
    }

    private void initOnScanDeviceListener() {
        mokoSupportAdaptor.setOnScanDeviceListener(this::deviceScannedEvent);
    }

    public void deviceScannedEvent(DeviceInfo deviceInfo) {
        if (SafetyObjectManager.isFitDangerZone(deviceInfo)) {
            appendToSmartagQueue(deviceInfo);
            SafetyObjectManager.checkAndAppendVirtualSmartagList(deviceInfo);
        }
    }

    private void appendToSmartagQueue(DeviceInfo deviceInfo) {
        smartagQueue.add(SmartagFactory.getSmartag(deviceInfo));
    }

    public boolean startScan() {
        if (!mokoSupportAdaptor.isBluetoothOn()) {
            mokoSupportAdaptor.requestTurnOnBluetooth(activity);
            return false;
        } else {
            return mokoSupportAdaptor.startScan();
        }
    }

    public void stopScan() {
        mokoSupportAdaptor.stopScan();
    }

    public void setLedRequest(int second) {
        mokoSupportAdaptor.initRequest();
        mokoSupportAdaptor.setLedRequest(second);
    }

    private void readyToSendRequestEvent() {
        smartagQueue.remove(targetSmartag);
        SafetyObjectManager.addSmartagList(targetSmartag);
        mokoSupportAdaptor.sendRequest();
        mokoSupportAdaptor.disconnectDevice();
    }

    private void disconnectDeviceEvent() {
        takeSmartagFromQueueAndConnect();
    }

    public void tryToTakeSmartagToConnect() {
        if (!isTryingConnection)
            takeSmartagFromQueueAndConnect();
    }

    private void takeSmartagFromQueueAndConnect() {
        if (smartagQueue.isEmpty()) {
            isTryingConnection = false;
            return;
        } else {
            isTryingConnection = true;
        }

        targetSmartag = smartagQueue.get(0);
        if (isNoRemainConnTimes(targetSmartag)) {
            smartagQueue.remove(targetSmartag);
            takeSmartagFromQueueAndConnect();
        } else {
            connectDevice(targetSmartag);
        }
    }

    private boolean isNoRemainConnTimes(Smartag smartag) {
        return smartag.getRemainConnectionTimes()<=0;
    }

    private void connectDevice(Smartag smartag) {
        smartag.minorRemainConnectionTimes();
        moveHeadToTail(smartag);
        mokoSupportAdaptor.connectDevice(smartag);
        isSent = true;
    }

    private void moveHeadToTail(Smartag smartag) {
        smartagQueue.remove(smartag);
        smartagQueue.add(smartag);
    }

    public List<DeviceInfo> getDeviceInfoList() {
        return mokoSupportAdaptor.getDeviceInfoList();
    }

    public static void sendAlertsToFilteredSmartags() {
        //TODO
    }

    public void showTheQueue() {
        for (int i = 0; i< smartagQueue.size(); i++) {
            Log.e("asasafety", ""+(i+1)+". "+ smartagQueue.get(i).mac);
        }
        Log.e("asasafety", "--------------------------------------------------------");
    }
}
