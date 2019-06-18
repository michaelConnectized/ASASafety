package com.asa.asasafety.Model;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;

import com.asa.asasafety.Object.Alert;
import com.asa.asasafety.Object.ApiObject;
import com.asa.asasafety.Object.DangerZone;
import com.asa.asasafety.Object.Worker;
import com.asa.asasafety.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.util.stream.Collectors;

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
        String resultJson = tryExecuteAndGetFromServer(getDangerZoneListUrl, postData);
        List<ApiObject> apiObjectList = tryJsonToApiObjectList("dangerZones", resultJson);
        return (List<DangerZone>)(List<?>)apiObjectList;
    }

    public List<DangerZone> getDangerZoneListDelta(String postData) {
        return getDangerZoneList(postData);
    }

    public List<Worker> getCurrentWorkerList(String postData) {
        String resultJson = tryExecuteAndGetFromServer(getCurrentWorkerListUrl, postData);
        List<ApiObject> apiObjectList = tryJsonToApiObjectList("workers", resultJson);
        return (List<Worker>)(List<?>)apiObjectList;
    }

    public List<Worker> getCurrentWorkerListDelta(String postData) {
        return getCurrentWorkerList(postData);
    }

    public String addAlert(String postData) {
        String resultJson = tryExecuteAndGetFromServer(addAlertUrl, postData);
        return resultJson;
    }

    public List<Alert> getAlerts(String postData) {
        String resultJson = tryExecuteAndGetFromServer(getAlertsUrl, postData);
        List<ApiObject> apiObjectList = tryJsonToApiObjectList("alertList", resultJson);
        return (List<Alert>)(List<?>)apiObjectList;
    }

    private List<ApiObject> tryJsonToApiObjectList(String apiJsonName, String json) {
        try {
            return jsonToApiObjectList(apiJsonName, json);
        } catch (JSONException e) {
            return new ArrayList<>();
        }
    }

    private List<ApiObject> jsonToApiObjectList(String apiJsonName, String json) throws JSONException {
        List<ApiObject> apiObjectList = new ArrayList<>();;
        if (isSuccess(json)) {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray apiObjectsJson = jsonObject.getJSONArray(apiJsonName);
            for (int i=0; i<apiObjectsJson.length(); i++) {
                apiObjectList.add(DangerZone.GetDangerZoneFromJson(apiObjectsJson.get(i).toString()));
            }
        }
        return apiObjectList;
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
