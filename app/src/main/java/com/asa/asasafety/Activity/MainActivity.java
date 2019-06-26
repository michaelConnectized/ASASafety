package com.asa.asasafety.Activity;

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
import com.asa.asasafety.MokoSupportAdaptor.MokoSupportManager;
import com.asa.asasafety.ObjectManager.SafetyObjectManager;
import com.asa.asasafety.R;
import com.asa.asasafety.Thread.CentralTimerThread;
import com.asa.asasafety.Thread.TimerEvent.AlertSmartagEvent;
import com.asa.asasafety.Thread.TimerEvent.GetDangerZoneEvent;
import com.asa.asasafety.utils.Utils;
import com.moko.support.entity.DeviceInfo;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    private int ledLightingTime = 30;

    private TextView tv_bt;
    private DeviceInfo targetDevice;
    private ApiConnectionAdaptor apiConnectionAdaptor;
    private MokoSupportManager mokoManager;
    private CentralTimerThread centralTimerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        defaultInit();

        initView();
        initVar();
        try {
            initTimeEvent();
            SafetyObjectManager.checkAndInitList();
            mokoManager.setLedRequest(ledLightingTime);
            mokoManager.startScan();
        } catch (Exception e) {

        }
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
        mokoManager = new MokoSupportManager(this);
        apiConnectionAdaptor = new ApiConnectionAdaptor(this.getResources());
        centralTimerThread = new CentralTimerThread();
    }

    public void initTimeEvent() throws JSONException {
        centralTimerThread.startThread();
        centralTimerThread.applyTimerEvent(new GetDangerZoneEvent(this));
        centralTimerThread.applyTimerEvent(new AlertSmartagEvent(mokoManager));
//        JSONObject tmpJsonObject = new JSONObject();
//        tmpJsonObject.put("deviceId", Utils.getSharePreference(this).getString("mac", ""));
//        SafetyObjectManager.setDangerZoneList(apiConnectionAdaptor.getDangerZoneList(tmpJsonObject.toString()));
//
//        Log.e("asasafety", tmpJsonObject.toString());
        //Log.e("asasafety", SafetyObjectManager.getDangerZoneList().toString());
    }

    public void start(View view) {
    }

    public void stop(View view) {
    }

    public void update(View view) {
    }

    public void Connect(View view) {

    }

    public void Disconnect(View view) {
        tv_bt.setText(mokoManager.getDeviceInfoList().get(0).mac);
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
