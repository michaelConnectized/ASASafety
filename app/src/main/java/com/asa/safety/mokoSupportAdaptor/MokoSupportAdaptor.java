package com.asa.safety.mokoSupportAdaptor;

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
import com.moko.support.MokoConstants;
import com.moko.support.MokoSupport;
import com.moko.support.callback.MokoScanDeviceCallback;
import com.moko.support.entity.DeviceInfo;
import com.asa.safety.service.MokoService;
import com.moko.support.entity.OrderType;
import com.moko.support.task.OrderTask;
import com.moko.support.task.OrderTaskResponse;
import com.moko.support.utils.MokoUtils;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

public class MokoSupportAdaptor implements MokoScanDeviceCallback {
    public String password;

    public enum Status {
        CONNECT_SUCCESS,
        READY_TO_SEND,
        DISCONNECTED
    }

    private MokoService mokoService;
    private List<DeviceInfo> deviceInfoList;
    private List<OrderTask> requestList;
    private Activity activity;
    private Status status;
    private onStatusChangedListener onStatusChangedListener;
    private onScanDeviceListener onScanDeviceListener;

    public MokoSupportAdaptor(Activity activity) {
        super();
        this.activity = activity;
        password = activity.getResources().getString(R.string.beacon_password);
        initMokoSupportApi();
        initCurrentConnectionStatus();
        initEmptyListener();
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
        mokoService.stopScanDevice();
    }

    public List<DeviceInfo> getDeviceInfoList() {
        return deviceInfoList;
    }

    public void connectDevice(DeviceInfo deviceInfo) {
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

    public void setTurnOffRequest() {
        requestList.add(mokoService.setClose());
    }

    public void sendRequest() {
        mokoService.sendOrder(requestList.toArray(new OrderTask[requestList.size()]));
    }

    @Override
    public void onStartScan() {
        deviceInfoList = new ArrayList<>();
    }

    @Override
    public void onScanDevice(DeviceInfo deviceInfo) {
        onScanDeviceListener.onScan(deviceInfo);
        if (!deviceInfoList.contains(deviceInfo))
            deviceInfoList.add(deviceInfo);
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

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (MokoConstants.ACTION_CONNECT_SUCCESS.equals(action)) {
                    sendGetLockStatusRequest();
                    setStatus(Status.CONNECT_SUCCESS);
                }
                if (MokoConstants.ACTION_CONNECT_DISCONNECTED.equals(action)) {
                    Log.e("asasafety", "ACTION_CONNECT_DISCONNECTED");
                    setStatus(Status.DISCONNECTED);
                }
                if (MokoConstants.ACTION_RESPONSE_TIMEOUT.equals(action)) {
                    Log.e("asasafety", "ACTION_RESPONSE_TIMEOUT");
                    setStatus(Status.DISCONNECTED);
                }
                if (MokoConstants.ACTION_RESPONSE_FINISH.equals(action)) {
                    Log.e("asasafety", "ACTION_RESPONSE_FINISH");
                }
                if (MokoConstants.ACTION_RESPONSE_SUCCESS.equals(action)) {
                    Log.e("asasafety", "ACTION_RESPONSE_SUCCESS");
                    handleConnection(intent);
                }
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    Log.e("asasafety", "ACTION_STATE_CHANGED");
                }
            }
        }
    };


    private String unLockResponse;
    private void handleConnection(Intent intent) {
        OrderTaskResponse response = (OrderTaskResponse) intent.getSerializableExtra(MokoConstants.EXTRA_KEY_RESPONSE_ORDER_TASK);
        OrderType orderType = response.orderType;
        int responseType = response.responseType;
        byte[] value = response.responseValue;

        Log.e("asasafety", ""+orderType);
        switch (orderType) {
            case lockState:
                String valueStr = MokoUtils.bytesToHexString(value);
                if ("00".equals(valueStr)) {
                    if (!TextUtils.isEmpty(unLockResponse)) {
                        unLockResponse = "";
                        MokoSupport.getInstance().disConnectBle();
                        startScan();
                    } else {
                        mokoService.sendOrder(mokoService.getUnLock());
                    }
                } else {
                    readyToSendRequestEvent();
                }
                break;
            case unLock:
                if (responseType == OrderTask.RESPONSE_TYPE_READ) {
                    sendPasswordToUnlock(value);
                }
                if (responseType == OrderTask.RESPONSE_TYPE_WRITE) {
                    unlockSuccessEvent();
                }
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

    private void readyToSendRequestEvent() {
        setStatus(Status.READY_TO_SEND);
    }

    private void setStatus(Status status) {
        this.status = status;
        onStatusChangedListener.onStatusChanged(status);
    }
}
