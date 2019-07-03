package com.asa.safety.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.asa.attend.model.AttendApiConnectionAdaptor;
import com.asa.attend.objectManager.AttendObjectManager;
import com.asa.attend.thread.event.TakeAttendanceEvent;
import com.asa.safety.macAddress.MacAddressManager;
import com.asa.safety.model.SafetyApiConnectionAdaptor;
import com.asa.safety.mokoSupportAdaptor.MokoSupportManager;
import com.asa.safety.objectManager.SafetyObjectManager;
import com.asa.safety.thread.event.AlertSmartagEvent;
import com.asa.safety.thread.event.AlertVirtualSmartagEvent;
import com.asa.safety.thread.event.GetDangerZoneEvent;
import com.asa.safety.thread.event.GetWorkerEvent;
import com.asa.utils.permission.Permission;
import com.asa.safety.R;
import com.asa.utils.thread.CentralTimerThread;
import com.asa.utils.Utils;
import com.moko.support.entity.DeviceInfo;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    private int ledLightingTime = 40;

    private TextView tv_bt;
    private DeviceInfo targetDevice;
    private SafetyApiConnectionAdaptor safetyApiConnectionAdaptor;
    private AttendApiConnectionAdaptor attendApiConnectionAdaptor;
    private MokoSupportManager mokoManager;
    private CentralTimerThread centralTimerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initVar();
        requestPermission();
        initMacAddress();
        initService();
        try {
            initTimeEvent();
            SafetyObjectManager.checkAndInitList();
            AttendObjectManager.checkAndInitList();
            new Handler().postDelayed(() -> {
                mokoManager.setLedRequest(ledLightingTime);
                mokoManager.startScan();
            }, 1000);

        } catch (Exception e) {
            Log.e("TestingUse", e.toString());
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
        centralTimerThread.startThread();
        centralTimerThread.applyTimerEvent(new TakeAttendanceEvent(attendApiConnectionAdaptor, Utils.getMacFromSharedPreference(this)));
        centralTimerThread.applyTimerEvent(new GetDangerZoneEvent(this));
        centralTimerThread.applyTimerEvent(new GetWorkerEvent(this));
        centralTimerThread.applyTimerEvent(new AlertSmartagEvent(mokoManager));
        centralTimerThread.applyTimerEvent(new AlertVirtualSmartagEvent(safetyApiConnectionAdaptor, Utils.getMacFromSharedPreference(this)));
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


    @Override
    public void onDestroy() {
        mokoManager.stopScan();
        super.onDestroy();
    }
}
