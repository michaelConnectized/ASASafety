package com.asa.safety.activity;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.asa.safety.attend.model.AttendApiConnectionAdaptor;
import com.asa.safety.attend.objectManager.AttendObjectManager;
import com.asa.safety.safety.macAddress.MacAddressManager;
import com.asa.safety.safety.model.SafetyApiConnectionAdaptor;
import com.asa.safety.safety.mokoSupportAdaptor.MokoSupportManager;
import com.asa.safety.safety.objectManager.SafetyObjectManager;
import com.asa.safety.safety.event.AlertSmartagEvent;
import com.asa.safety.utils.permission.Permission;
import com.asa.safety.R;
import com.asa.safety.utils.thread.CentralTimerThread;
import com.asa.safety.utils.Utils;
import com.asa.safety.utils.thread.event.RefleshBluetoothEvent;
import com.crashlytics.android.Crashlytics;
import com.moko.support.entity.DeviceInfo;

import io.fabric.sdk.android.Fabric;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    private int ledLightingTime = 50;

    private TextView tv_bt;
    private DeviceInfo targetDevice;
    private SafetyApiConnectionAdaptor safetyApiConnectionAdaptor;
    private AttendApiConnectionAdaptor attendApiConnectionAdaptor;
    private MokoSupportManager mokoManager;
    private CentralTimerThread centralTimerThread;

    private boolean CentralTimerThreadStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Fabric.with(this, new Crashlytics());
        try {
            initVar();
            requestPermission();
            initMacAddress();
            initService();
            SafetyObjectManager.checkAndInitList();
            AttendObjectManager.checkAndInitList();

            initTimeEvent();
            startScan();
        } catch (Exception e) {
            Log.e("TestingUse", e.toString());
            finish();
        }
    }

    public void initVar() {
        mokoManager = new MokoSupportManager(this);
        safetyApiConnectionAdaptor = new SafetyApiConnectionAdaptor(this.getResources());
        attendApiConnectionAdaptor = new AttendApiConnectionAdaptor(this.getResources());
        centralTimerThread = new CentralTimerThread();
        AttendObjectManager.initLocationManager(this);
    }

    private void initService() {
        AttendObjectManager.getMyLocationManager().enableGpsService();

    }

    public void initTimeEvent() throws JSONException {
        if (CentralTimerThreadStarted) {
            return;
        }

        centralTimerThread.startThread();
//        centralTimerThread.applyTimerEvent(new TakeAttendanceEvent(attendApiConnectionAdaptor, Utils.getMacFromSharedPreference(this)));
//        centralTimerThread.applyTimerEvent(new GetDangerZoneEvent(this));
//        centralTimerThread.applyTimerEvent(new GetWorkerEvent(this));
        centralTimerThread.applyTimerEvent(new AlertSmartagEvent(mokoManager));
//        centralTimerThread.applyTimerEvent(new AlertVirtualSmartagEvent(safetyApiConnectionAdaptor, Utils.getMacFromSharedPreference(this)));
        centralTimerThread.applyTimerEvent(new RefleshBluetoothEvent(mokoManager));
        CentralTimerThreadStarted = true;
    }

    public void requestPermission() {
        String[] permissionTypes = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.READ_CONTACTS
        };

        Permission permission = new Permission(this);
        permission.checkPermission(permissionTypes);
    }

    public void initMacAddress() {
        MacAddressManager macAddressManager = new MacAddressManager(this);
        macAddressManager.saveMacAddr();
        ((TextView)findViewById(R.id.tv_mac)).setText(Utils.getMacFromSharedPreference(this));
    }

    public void startScan() {
        new Handler().postDelayed(() -> {
            //TODO
            mokoManager.setEditTxRequest(ledLightingTime);
            mokoManager.startScan();
        }, 1000);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mokoManager.stopScan();
        mokoManager.unregisterReceiver();
        mokoManager.unBindService();
        centralTimerThread.stopThread();
        AttendObjectManager.getMyLocationManager().stopGpsService();
    }

    @Override
    public void onStop() {
        super.onStop();
        finish();
    }
}
