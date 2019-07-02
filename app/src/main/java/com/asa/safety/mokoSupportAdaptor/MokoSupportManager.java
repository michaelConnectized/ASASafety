package com.asa.safety.mokoSupportAdaptor;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.asa.attend.objectManager.AttendObjectManager;
import com.asa.safety.object.Smartag;
import com.asa.safety.object.SmartagFactory;
import com.asa.safety.objectManager.SafetyObjectManager;
import com.asa.safety.R;
import com.asa.utils.Utils;
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
        scannedEventForSafety(deviceInfo);
        scannedEventForAttend(deviceInfo);
    }

    private void scannedEventForSafety(DeviceInfo deviceInfo) {
        if (SafetyObjectManager.isFitDangerZone(deviceInfo, Utils.getMacFromSharedPreference(activity))) {
            checkAndAppendToSmartagQueue(deviceInfo);
            SafetyObjectManager.checkAndAppendVirtualSmartagList(deviceInfo);
        }
    }

    private void scannedEventForAttend(DeviceInfo deviceInfo) {
        if (AttendObjectManager.isLocationSet()) {
            AttendObjectManager.checkAndAddFilteredAttendanceList(deviceInfo, Utils.getMacFromSharedPreference(activity));
        }
    }

    private void checkAndAppendToSmartagQueue(DeviceInfo deviceInfo) {
        Smartag smartag = SmartagFactory.getSmartag(deviceInfo);
        if (!smartagQueue.contains(smartag) && !SafetyObjectManager.filteredSmartags.contains(smartag))
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

    public void setCloseRequest() {
        mokoSupportAdaptor.initRequest();
        mokoSupportAdaptor.setTurnOffRequest();
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

    public void simpleConnect(DeviceInfo deviceInfo) {
        Log.e("asasafety", "simpleConnect: "+deviceInfo.mac);
        mokoSupportAdaptor.connectDevice(deviceInfo);
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

    public void updateUI() {
        String list = "";
        if (!smartagQueue.isEmpty())
            for (int i=0; i<smartagQueue.size(); i++) {
                list += smartagQueue.get(i).mac+", Try Times:"+(3-smartagQueue.get(i).getRemainConnectionTimes())+"\n";
            }
        ((TextView)activity.findViewById(R.id.tv_queue_list)).setText(list);
    }
}
