package com.asa.safety.utils.thread.event;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.asa.safety.R;
import com.asa.safety.safety.model.SafetyApiConnectionAdaptor;
import com.asa.safety.safety.mokoSupportAdaptor.MokoSupportManager;
import com.asa.safety.safety.object.DangerZone;
import com.asa.safety.safety.objectManager.SafetyObjectManager;
import com.asa.safety.utils.Utils;
import com.moko.support.MokoSupport;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RefleshBluetoothEvent extends TimerEvent {
    protected String eventName = "RefleshBluetoothEvent";

    private final int refreshTime = 60 * 60;

    private MokoSupportManager mokoManager;

    public RefleshBluetoothEvent(MokoSupportManager mokoManager) {
        super(60 * 30, false);
        this.mokoManager = mokoManager;
    }

    protected void event() {
        refreshBluetooth();
    }

    private void refreshBluetooth() {
        stopScan();
        startScan();
    }

    public void startScan() {
        new Handler().post(() -> mokoManager.startScan());
    }

    public void stopScan() {
        mokoManager.stopScan();
    }
}
