package com.asa.asasafety.Thread.TimerEvent;

import android.app.Activity;

import com.asa.asasafety.Model.ApiConnectionAdaptor;
import com.asa.asasafety.Object.DangerZone;
import com.asa.asasafety.ObjectManager.SafetyObjectManager;
import com.asa.asasafety.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GetDangerZoneEvent extends TimerEvent {
    private final int definition_non_existed_time = 5;
    private final int definition_existed_time = 3 * 60;

    private ApiConnectionAdaptor apiConnectionAdaptor;
    private JSONObject postData;

    public GetDangerZoneEvent(Activity activity) throws JSONException {
        super(5, true);
        this.apiConnectionAdaptor = new ApiConnectionAdaptor(activity.getResources());
        postData = new JSONObject();
        postData.put("deviceId", Utils.getSharePreference(activity).getString("mac", ""));
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
        List<DangerZone> dangerZoneList = apiConnectionAdaptor.getDangerZoneList(postData.toString());
        SafetyObjectManager.setDangerZoneList(dangerZoneList);
    }
}
