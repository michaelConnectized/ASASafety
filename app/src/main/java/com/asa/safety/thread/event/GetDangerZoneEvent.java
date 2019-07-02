package com.asa.safety.thread.event;

import android.app.Activity;
import android.widget.TextView;

import com.asa.safety.model.SafetyApiConnectionAdaptor;
import com.asa.safety.object.DangerZone;
import com.asa.safety.objectManager.SafetyObjectManager;
import com.asa.safety.R;
import com.asa.utils.Utils;
import com.asa.utils.thread.event.TimerEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GetDangerZoneEvent extends TimerEvent {
    protected String eventName = "GetDangerZoneEvent";

    private final int definition_non_existed_time = 5;
    private final int definition_existed_time = 3 * 60;

    private SafetyApiConnectionAdaptor safetyApiConnectionAdaptor;
    private JSONObject postData;
    private Activity activity;

    public GetDangerZoneEvent(Activity activity) throws JSONException {
        super(5, true);
        this.activity = activity;
        this.safetyApiConnectionAdaptor = new SafetyApiConnectionAdaptor(activity.getResources());
        postData = new JSONObject();
        postData.put("deviceId", Utils.getMacFromSharedPreference(activity));
    }

    protected void event() {
        setDangerZonesFromServer();
        changeAlarmTimeByDangerZoneExistence();
    }

    private void changeAlarmTimeByDangerZoneExistence() {
        if (SafetyObjectManager.getDangerZoneList().size()==0) {
            alarmingTime = definition_non_existed_time;
        } else {
            alarmingTime = definition_existed_time;
        }
    }

    private void setDangerZonesFromServer() {
        List<DangerZone> dangerZoneList = safetyApiConnectionAdaptor.getDangerZoneList(postData.toString());
        SafetyObjectManager.setDangerZoneList(dangerZoneList);
        updateUI2CurrentTime();
    }

    private void updateUI2CurrentTime() {
        if (!SafetyObjectManager.getDangerZoneList().isEmpty())
            ((TextView)activity.findViewById(R.id.tv_update_worker)).setText(SafetyObjectManager.getDangerZoneList().get(0).getLastUpdated());
    }
}
