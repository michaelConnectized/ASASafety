package com.asa.safety.safety.mokoSupportAdaptor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.asa.safety.R;
import com.asa.safety.safety.service.MokoService;
import com.asa.safety.utils.Utils;
import com.moko.support.MokoConstants;
import com.moko.support.MokoSupport;
import com.moko.support.callback.MokoScanDeviceCallback;
import com.moko.support.entity.DeviceInfo;
import com.moko.support.entity.OrderType;
import com.moko.support.entity.SlotEnum;
import com.moko.support.task.OrderTask;
import com.moko.support.task.OrderTaskResponse;
import com.moko.support.utils.MokoUtils;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

public class MokoSupportAdaptor implements MokoScanDeviceCallback {
    private final String tag = "MokoSupportAdaptor";

    public String password;

    public enum Status {
        CONNECT_SUCCESS,
        READY_TO_SEND,
        WRITE_SUCCESSFUL,
        DISCONNECTED
    }

    private MokoService mokoService;
    private List<DeviceInfo> deviceInfoList;
    private List<OrderTask> requestList;
    private Activity activity;
    private Status status;
    private onStatusChangedListener onStatusChangedListener;
    private onScanDeviceListener onScanDeviceListener;

    private List<String> editedDeviceList;
    private String currentDevice = "";

    private int setCount = 0;
    int battery;

    public MokoSupportAdaptor(Activity activity) {
        super();
        this.activity = activity;
        password = activity.getResources().getString(R.string.beacon_password);
        initMokoSupportApi();
        initCurrentConnectionStatus();
        initEmptyListener();
        initEditedDeviceList();
    }

    private void initEditedDeviceList() {
        editedDeviceList = new ArrayList<>();
        try {
            String json = Utils.getSharePreference(activity).getString("editedDeviceList", "");
            Log.e(tag, "Current json: "+json);
            JSONArray ja = new JSONArray(json);
            int len = ja.length();
            for (int i=0;i<len;i++){
                editedDeviceList.add(ja.get(i).toString());
            }
        } catch (Exception e) {
            Log.e(tag, e.toString());
        }
    }

    private void saveEditedDevices() {
        JSONArray jsonArray = new JSONArray();
        for (String device: editedDeviceList) {
            jsonArray.put(device);
        }
        Utils.getSharePreference(activity).edit().putString("editedDeviceList", jsonArray.toString()).commit();
        setCount++;
        Log.e(tag, "device Put");
    }

    private void initMokoSupportApi() {
        MokoSupport.getInstance().init(activity);
    }

    private void initCurrentConnectionStatus() {
        status = Status.DISCONNECTED;
    }

    private void initEmptyListener() {
        onStatusChangedListener = (status) -> {};
        onScanDeviceListener = (deviceInfo) ->  {};
    }

    public interface onStatusChangedListener {
        void onStatusChanged(Status status);
    }

    public interface onScanDeviceListener {
        void onScan(DeviceInfo deviceInfo);
    }

    public void setOnStatusChangedListener(onStatusChangedListener eventListener) {
        onStatusChangedListener = eventListener;
    }

    public void setOnScanDeviceListener(onScanDeviceListener listener) {
        onScanDeviceListener = listener;
    }


    public void startService() {
        Intent intent = new Intent(activity, MokoService.class);
        activity.startService(intent);
        activity.bindService(intent, getServiceConnection(), BIND_AUTO_CREATE);
    }

    public void unBindService() {
        try {
            activity.unbindService(getServiceConnection());
        } catch (Exception e) {
            Log.e(tag, e.toString());
        }

    }

    public boolean isBluetoothOn() {
        return MokoSupport.getInstance().isBluetoothOpen();
    }

    public void requestTurnOnBluetooth(Activity activity) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, MokoConstants.REQUEST_CODE_ENABLE_BT);
    }

    public boolean startScan() {
        if (isBluetoothOn()) {
            mokoService.startScanDevice(this);
        }
        return isBluetoothOn();
    }

    public void stopScan() {
        if (mokoService!=null)
         mokoService.stopScanDevice();
    }

    public List<DeviceInfo> getDeviceInfoList() {
        return deviceInfoList;
    }

    public void connectDevice(DeviceInfo deviceInfo) {
        Log.e(tag, "Try connect to: "+deviceInfo.mac);
        currentDevice = deviceInfo.mac;
        mokoService.mHandler.removeMessages(0);
        mokoService.connDevice(deviceInfo.mac);
    }

    public void disconnectDevice() {
        MokoSupport.getInstance().disConnectBle();
    }

    public void sendGetLockStatusRequest() {
        mokoService.mHandler.postDelayed(()-> mokoService.sendOrder(mokoService.getLockState()), 1000);
    }

    public void initRequest() {
        requestList = new ArrayList<>();
    }

    public void setLedRequest(int time_second) {
        requestList.add(mokoService.setLEDInfo(true, true, time_second*1000, 25000, 50));
    }

    public void setEditTxPowerRequest() {
        int timeout = 30;
        int advInterval = 1000;
        int advTxPower = 0;
        int txPower = 0;

        requestList.add(mokoService.setFastMode(true, timeout, advInterval, advTxPower, txPower));
        requestList.add(mokoService.setSlowMode(advInterval, advTxPower, txPower));
    }

    public void setTurnOffRequest() {
        requestList.add(mokoService.setClose());
    }

    public void sendRequest() {
        mokoService.sendOrder(requestList.toArray(new OrderTask[requestList.size()]));
        editedDeviceList.add(currentDevice);
        saveEditedDevices();
        Log.e(tag+"1", String.valueOf(setCount));
        Utils.saveInTxt(currentDevice+",,"+battery+",0,b'ITT',"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"\r\n");
    }

    @Override
    public void onStartScan() {
        deviceInfoList = new ArrayList<>();
    }

    @Override
    public void onScanDevice(DeviceInfo deviceInfo) {
        if (!deviceInfoList.contains(deviceInfo)) {
            if (editedDeviceList.contains(deviceInfo.mac)) {
                return;
            }
            deviceInfoList.add(deviceInfo);
            onScanDeviceListener.onScan(deviceInfo);
            Log.e(tag+"abc", deviceInfo.mac+" is scanned");
            Log.e("deviceList", deviceInfoList.toString());
        }

    }

    @Override
    public void onStopScan() {

    }

    private ServiceConnection getServiceConnection() {
        return new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mokoService = ((MokoService.LocalBinder) service).getService();
                // 注册广播接收器
                IntentFilter filter = new IntentFilter();
                filter.addAction(MokoConstants.ACTION_CONNECT_SUCCESS);
                filter.addAction(MokoConstants.ACTION_CONNECT_DISCONNECTED);
                filter.addAction(MokoConstants.ACTION_RESPONSE_SUCCESS);
                filter.addAction(MokoConstants.ACTION_RESPONSE_TIMEOUT);
                filter.addAction(MokoConstants.ACTION_RESPONSE_FINISH);
                filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                filter.setPriority(100);
                activity.registerReceiver(mReceiver, filter);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
    }

    public void unregisterReceiver() {
        try {
            activity.unregisterReceiver(mReceiver);
        } catch (Exception e) {
            Log.e(tag, e.toString());
        }

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (MokoConstants.ACTION_CONNECT_SUCCESS.equals(action)) {
                    sendGetLockStatusRequest();
                    Log.e(tag, "ACTION_CONNECT_SUCCESS");
                    setStatus(Status.CONNECT_SUCCESS);
                }
                if (MokoConstants.ACTION_CONNECT_DISCONNECTED.equals(action)) {
                    Log.e(tag, "ACTION_CONNECT_DISCONNECTED");
                    setStatus(Status.DISCONNECTED);
                }
                if (MokoConstants.ACTION_RESPONSE_TIMEOUT.equals(action)) {
                    Log.e(tag, "ACTION_RESPONSE_TIMEOUT");
                    responseTimeoutEvent(intent);
                }
                if (MokoConstants.ACTION_RESPONSE_FINISH.equals(action)) {
                    Log.e(tag, "ACTION_RESPONSE_FINISH");
                }
                if (MokoConstants.ACTION_RESPONSE_SUCCESS.equals(action)) {
                    Log.e(tag, "ACTION_RESPONSE_SUCCESS");
                    responseSuccessEvent(intent);
                }
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    Log.e("asasafety", "ACTION_STATE_CHANGED");
                }
            }
        }
    };

    private void responseTimeoutEvent(Intent intent) {
        OrderTaskResponse response = (OrderTaskResponse) intent.getSerializableExtra(MokoConstants.EXTRA_KEY_RESPONSE_ORDER_TASK);
        OrderType orderType = response.orderType;
        int responseType = response.responseType;
        byte[] value = response.responseValue;

        Log.e(tag, ""+orderType);
        disconnectDevice();
        setStatus(Status.DISCONNECTED);
    }

    private void askForBattery() {
        mokoService.sendOrder(mokoService.getBattery());
    }


    private String unLockResponse;
    private void responseSuccessEvent(Intent intent) {
        OrderTaskResponse response = (OrderTaskResponse) intent.getSerializableExtra(MokoConstants.EXTRA_KEY_RESPONSE_ORDER_TASK);
        OrderType orderType = response.orderType;
        int responseType = response.responseType;
        byte[] value = response.responseValue;

//        Log.e(tag, ""+orderType);
        switch (orderType) {
            case lockState:
                String valueStr = MokoUtils.bytesToHexString(value);
                if ("00".equals(valueStr)) {
                    if (!TextUtils.isEmpty(unLockResponse)) {
                        unLockResponse = "";
                    }
                    Log.e(tag, ""+orderType+": Get unlock data");
                    mokoService.sendOrder(mokoService.getUnLock());
                } else {
                    Log.e(tag, ""+orderType+": Ready to send data");
                    askForBattery();
                }
                break;
            case unLock:
                if (responseType == OrderTask.RESPONSE_TYPE_READ) {
                    Log.e(tag, ""+orderType+": Send password to unlock");
                    sendPasswordToUnlock(value);
                }
                if (responseType == OrderTask.RESPONSE_TYPE_WRITE) {
                    Log.e(tag, ""+orderType+": Unlock Successful");
                    unlockSuccessEvent();
                }
                break;
            case writeConfig:
                Log.e(tag, ""+orderType+": Send turning on LED request successful");
                setStatus(Status.WRITE_SUCCESSFUL);
            case battery:
                if (MokoUtils.bytesToHexString(response.responseValue).equals("eb69000100")) {
                    return;
                }
                battery = Integer.parseInt(MokoUtils.bytesToHexString(response.responseValue), 16);
                setStatus(Status.READY_TO_SEND);
                break;
            default:
        }
    }

    private void sendPasswordToUnlock(byte[] responseValue) {
        unLockResponse = MokoUtils.bytesToHexString(responseValue);
        Log.e("asasafety", "Sent pwd: "+password);
        mokoService.sendOrder(mokoService.setConfigNotify(), mokoService.setUnLock(password, responseValue));
    }

    private void unlockSuccessEvent() {
        sendGetLockStatusRequest();
        setStatus(Status.CONNECT_SUCCESS);
    }

    private void setStatus(Status status) {
        this.status = status;
        onStatusChangedListener.onStatusChanged(status);
    }
}
