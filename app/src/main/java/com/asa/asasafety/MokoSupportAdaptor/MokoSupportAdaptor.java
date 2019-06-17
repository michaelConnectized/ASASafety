package com.asa.asasafety.MokoSupportAdaptor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.moko.support.MokoConstants;
import com.moko.support.MokoSupport;
import com.moko.support.callback.MokoScanDeviceCallback;
import com.moko.support.entity.DeviceInfo;
import com.asa.asasafety.service.MokoService;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

public class MokoSupportAdaptor implements MokoScanDeviceCallback {
    private MokoService mokoService;
    private List<DeviceInfo> deviceInfoList;
    private Activity activity;

    MokoSupport mokoSupport;
    public MokoSupportAdaptor(Activity activity) {
        super();
        this.activity = activity;
        MokoSupport.getInstance().init(activity);
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
        mokoService.stopScanDevice();
        mokoService.connDevice(deviceInfo.mac);
    }

    public void disconnectDevice() {
        MokoSupport.getInstance().disConnectBle();
    }

    public void sendGetLockStatusRequest() {
        mokoService.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mokoService.sendOrder(mokoService.getLockState());
            }
        }, 1000);
    }

    public void sendLedRequest(int time_second) {
        mokoService.sendOrder(mokoService.setLEDInfo(true, true, time_second*1000, 25000, 50));
    }



    @Override
    public void onStartScan() {
        deviceInfoList = new ArrayList<>();
    }

    @Override
    public void onScanDevice(DeviceInfo deviceInfo) {
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
                }
                if (MokoConstants.ACTION_CONNECT_DISCONNECTED.equals(action)) {
                    Log.e("asasafety", "ACTION_CONNECT_DISCONNECTED");
                }
                if (MokoConstants.ACTION_RESPONSE_TIMEOUT.equals(action)) {
                    Log.e("asasafety", "ACTION_RESPONSE_TIMEOUT");
                }
                if (MokoConstants.ACTION_RESPONSE_FINISH.equals(action)) {
                    Log.e("asasafety", "ACTION_RESPONSE_FINISH");
                }
                if (MokoConstants.ACTION_RESPONSE_SUCCESS.equals(action)) {
                    Log.e("asasafety", "ACTION_RESPONSE_SUCCESS");
                    sendLedRequest(30);
                    disconnectDevice();
                }
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    Log.e("asasafety", "ACTION_STATE_CHANGED");
                }
            }
        }
    };
}
