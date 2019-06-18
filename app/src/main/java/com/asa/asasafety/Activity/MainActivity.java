package com.asa.asasafety.Activity;

import android.content.Context;
import android.net.MacAddress;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.asa.asasafety.MacAddress.MacAddressManager;
import com.asa.asasafety.Model.ApiConnectionAdaptor;
import com.asa.asasafety.MokoSupportAdaptor.MokoSupportAdaptor;
import com.asa.asasafety.Object.DangerZone;
import com.asa.asasafety.ObjectManager.SafetyObjectManager;
import com.asa.asasafety.R;
import com.asa.asasafety.utils.Utils;
import com.moko.support.entity.DeviceInfo;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView tv_bt;
    private MokoSupportAdaptor mokotAdaptor;
    private DeviceInfo targetDevice;
    private ApiConnectionAdaptor apiConnectionAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        defaultInit();

        initView();
        initVar();
        mokotAdaptor.startService();
    }

    public void defaultInit() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void initView() {
        tv_bt = findViewById(R.id.bt);
    }

    public void initVar() {
        mokotAdaptor = new MokoSupportAdaptor(this);
        apiConnectionAdaptor = new ApiConnectionAdaptor(this.getResources());
//        SafetyObjectManager.setDangerZoneList(apiConnectionAdaptor.getDangerZoneList("{\n" +
//                "\t\"projectId\": \"58d34ce8143b73986de6eba2\"\n" +
//                "}"));
//        Log.e("AsaSafety", apiConnectionAdaptor.getDangerZoneList("{\n" +
//                "\t\"projectId\": \"58d34ce8143b73986de6eba2\"\n" +
//                "}"));
        //Log.e("asasafety", SafetyObjectManager.getDangerZoneList().toString());

    }





    public void start(View view) {
        if (!mokotAdaptor.isBluetoothOn()) {
            mokotAdaptor.requestTurnOnBluetooth(this);
            tv_bt.setText("Ask for Bluetooth");
        } else {
            if (mokotAdaptor.startScan()) {
                tv_bt.setText("Scanning");
            }
        }
    }

    public void stop(View view) {
        mokotAdaptor.stopScan();
        tv_bt.setText("Stopped");
    }

    public void update(View view) {
        List<DeviceInfo> deviceInfos = mokotAdaptor.getDeviceInfoList();
        String item = "E4:64:8E:54:34:EF is not found!\n";
        for (int i=0; i<deviceInfos.size(); i++) {
            if (deviceInfos.get(i).mac.equals("E4:64:8E:54:34:EF")) {
                item = "E4:64:8E:54:34:EF is found!\n";
                targetDevice = deviceInfos.get(i);
                break;
            }
        }
        tv_bt.setText(item);
    }

    public void Connect(View view) {
        mokotAdaptor.connectDevice(targetDevice);
        tv_bt.setText("Connecting...");
    }

    public void Disconnect(View view) {
        mokotAdaptor.disconnectDevice();
        tv_bt.setText("Disconnecting...");
    }

    public void Lighting(View view) {
        MacAddressManager macManager = new MacAddressManager(this);
        if (macManager.saveMacAddr()) {
            Log.e("asasafety", "Mac: "+ Utils.getSharePreference(this).getString("mac", ""));
        } else {
            Log.e("asasafety", "Mac Saving Failed");
        }
    }

    public void beDark(View view) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
