package com.asa.asasafety.Model;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;

import com.asa.asasafety.Object.DangerZone;
import com.asa.asasafety.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApiConnectionAdaptor {
    private final String tag = "ApiConnectionAdaptor";

    private Resources res;
    private String baseUrl;
    private String getDangerZoneListUrl;
    private String getCurrentWorkerListUrl;
    private String addAlertUrl;
    private String getAlertsUrl;

    private boolean isHttps;

    public ApiConnectionAdaptor(Resources res) {
        this.res = res;
        initVar();
    }

    private void initVar() {
        baseUrl = res.getString(R.string.api_base_url);
        getDangerZoneListUrl = baseUrl + res.getString(R.string.get_danger_zone_list_url);
        getCurrentWorkerListUrl = baseUrl + res.getString(R.string.get_current_worker_list_url);
        addAlertUrl = baseUrl + res.getString(R.string.add_alert_url);
        getAlertsUrl = baseUrl + res.getString(R.string.get_alerts_url);

        isHttps = res.getBoolean(R.bool.is_https);
    }

    public List<DangerZone> getDangerZoneList(String postData) {
        List<DangerZone> dangerZoneList = new ArrayList<>();;
        String resultJson = tryExecuteAndGetFromServer(getDangerZoneListUrl, postData);
        if (isSuccess(resultJson)) {
            try {
                JSONObject jsonObject = new JSONObject(resultJson);
                JSONArray dangerZonesJson = jsonObject.getJSONArray("dangerZones");
                for (int i=0; i<dangerZonesJson.length(); i++) {
                    dangerZoneList.add(DangerZone.GetDangerZoneFromJson(dangerZonesJson.get(i).toString()));
                }
            } catch (Exception e) {
                dangerZoneList = new ArrayList<>();
            }
        } else {
            dangerZoneList = new ArrayList<>();
        }
        return dangerZoneList;
    }

    public String getDangerZoneListDelta(String postData) {
        return tryExecuteAndGetFromServer(getDangerZoneListUrl, postData);
    }

    public String getCurrentWorkerList(String postData) {
        return tryExecuteAndGetFromServer(getCurrentWorkerListUrl, postData);
    }

    public String getCurrentWorkerListDelta(String postData) {
        return tryExecuteAndGetFromServer(getCurrentWorkerListUrl, postData);
    }

    public String addAlert(String postData) {
        return tryExecuteAndGetFromServer(addAlertUrl, postData);
    }

    public String getAlerts(String postData) {
        return tryExecuteAndGetFromServer(getAlertsUrl, postData);
    }

    public boolean isSuccess(String json) {
        boolean success = false;
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.getString("result").equals("success")) {
                success = true;
            }
        } catch (Exception e) {
            Log.e(tag, e.toString());
        }
        return success;
    }

    private String tryExecuteAndGetFromServer(String fullUrl, String postData) {
        String response;
        try {
            response = getApiConnection(fullUrl, postData).execute().get();
        } catch (Exception e) {
            response = "";
        }
        return response;
    }

    private ApiConnection getApiConnection(String fullUrl, String postData) {
        ApiConnection apiConnection;
        if (isHttps)
            apiConnection = new HttpsApiConnection(fullUrl, postData);
        else
            apiConnection = new HttpApiConnection(fullUrl, postData);
        return apiConnection;
    }

}
