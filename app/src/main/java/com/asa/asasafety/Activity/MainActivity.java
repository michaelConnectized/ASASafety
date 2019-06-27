package com.asa.asasafety.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.asa.asasafety.MacAddress.MacAddressManager;
import com.asa.asasafety.Model.ApiConnectionAdaptor;
import com.asa.asasafety.MokoSupportAdaptor.MokoSupportManager;
import com.asa.asasafety.ObjectManager.SafetyObjectManager;
import com.asa.asasafety.Permission.Permission;
import com.asa.asasafety.R;
import com.asa.asasafety.Thread.CentralTimerThread;
import com.asa.asasafety.Thread.TimerEvent.AlertSmartagEvent;
import com.asa.asasafety.Thread.TimerEvent.AlertVirtualSmartagEvent;
import com.asa.asasafety.Thread.TimerEvent.GetDangerZoneEvent;
import com.asa.asasafety.Thread.TimerEvent.GetWorkerEvent;
import com.asa.asasafety.utils.Utils;
import com.moko.support.entity.DeviceInfo;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    private int ledLightingTime = 40;

    private TextView tv_bt;
    private DeviceInfo targetDevice;
    private ApiConnectionAdaptor apiConnectionAdaptor;
    private MokoSupportManager mokoManager;
    private CentralTimerThread centralTimerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().hide(); //hide the title bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initVar();
        requestPermission();
        initMacAddress();
        try {
            initTimeEvent();
            SafetyObjectManager.checkAndInitList();
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
        apiConnectionAdaptor = new ApiConnectionAdaptor(this.getResources());
        centralTimerThread = new CentralTimerThread();
    }

    public void initTimeEvent() throws JSONException {
        centralTimerThread.startThread();
        centralTimerThread.applyTimerEvent(new GetDangerZoneEvent(this));
        centralTimerThread.applyTimerEvent(new GetWorkerEvent(this));
        centralTimerThread.applyTimerEvent(new AlertSmartagEvent(mokoManager));
        centralTimerThread.applyTimerEvent(new AlertVirtualSmartagEvent(apiConnectionAdaptor, Utils.getSharePreference(this).getString("mac", "")));
    }

    public void requestPermission() {
        String[] permissionTypes = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_CONTACTS
        };

        Permission permission = new Permission(this);
        permission.checkPermission(permissionTypes);
    }

    public void initMacAddress() {
        MacAddressManager macAddressManager = new MacAddressManager(this);
        macAddressManager.saveMacAddr();
        ((TextView)findViewById(R.id.tv_mac)).setText(Utils.getSharePreference(this).getString("mac", "N/A"));
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

    @Override
    public void onDestroy() {
        mokoManager.stopScan();
        super.onDestroy();
    }
}
