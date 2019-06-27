package com.asa.asasafety.Model;

import android.content.res.Resources;
import android.util.Log;

import com.asa.asasafety.Object.Alert;
import com.asa.asasafety.Object.ApiObject;
import com.asa.asasafety.Object.DangerZone;
import com.asa.asasafety.Object.VirtualSmartag;
import com.asa.asasafety.Object.Worker;
import com.asa.asasafety.ObjectManager.SafetyObjectManager;
import com.asa.asasafety.R;
import com.asa.asasafety.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ApiConnectionAdaptor {
    private static final String tag = "ApiConnectionAdaptor";

    private Resources res;
    private String baseUrl;
    private String getDangerZoneListUrl;
    private String getCurrentWorkerListUrl;
    private String addAlertUrl;
    private String getAlertsUrl;

    private String workerLastUpdated = "N/A";

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
        List<ApiObject> apiObjectList = tryJsonToApiObjectList("dangerZones", resultJson, new DangerZone());
        return (List<DangerZone>)(List<?>)apiObjectList;
    }

    public List<DangerZone> getDangerZoneListDelta(String postData) {
        return getDangerZoneList(postData);
    }

    public List<Worker> getCurrentWorkerListAndSetLastUpdated(String postData) {
        String resultJson = tryExecuteAndGetFromServer(getCurrentWorkerListUrl, postData);
        List<ApiObject> apiObjectList = tryJsonToApiObjectList("workers", resultJson, new Worker());
        workerLastUpdated = getLastUpdated(resultJson);
        return (List<Worker>)(List<?>)apiObjectList;
    }

    public List<Worker> getCurrentWorkerListDelta(String postData) {
        return getCurrentWorkerListAndSetLastUpdated(postData);
    }

    public boolean addAlert(String postData) {
        String resultJson = tryExecuteAndGetFromServer(addAlertUrl, postData);
        return isSuccess(resultJson);
    }

    public List<Alert> getAlerts(String postData) {
        String resultJson = tryExecuteAndGetFromServer(getAlertsUrl, postData);
        List<ApiObject> apiObjectList = tryJsonToApiObjectList("alertList", resultJson, new Alert());
        return (List<Alert>)(List<?>)apiObjectList;
    }



    private List<ApiObject> tryJsonToApiObjectList(String apiJsonName, String json, ApiObject objClass) {
        try {
            return jsonToApiObjectList(apiJsonName, json, objClass);
        } catch (JSONException e) {
            Log.e(tag, e.toString());
            return new ArrayList<>();
        }
    }

    private List<ApiObject> jsonToApiObjectList(String apiJsonName, String json, ApiObject objClass) throws JSONException {
        List<ApiObject> apiObjectList = new ArrayList<>();;
        if (isSuccess(json)) {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray apiObjectsJson = jsonObject.getJSONArray(apiJsonName);
            for (int i=0; i<apiObjectsJson.length(); i++) {
                apiObjectList.add(objClass.getObjectFromJson(apiObjectsJson.get(i).toString()));
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

    private String getLastUpdated(String json) {
        String lastUpdated = "FAIL";
        try {
            JSONObject jsonObject = new JSONObject(json);
            lastUpdated = jsonObject.getString("lastUpdated");
        } catch (Exception e) {
            Log.e(tag, e.toString());
        }
        return lastUpdated;
    }

    private String tryExecuteAndGetFromServer(String fullUrl, String postData) {
        String response;
        try {
            response = getApiConnection(fullUrl, postData).execute().get();
        } catch (Exception e) {
            Log.e(tag, "tryExecuteAndGetFromServer: "+e.toString());
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

    public void sendAlertsToServer(String localMacAddress) {
        if (SafetyObjectManager.filteredVirtualSmartags.isEmpty()) {
            return;
        }
        for (int i=0; i<SafetyObjectManager.filteredVirtualSmartags.size(); i++) {
            VirtualSmartag smartag = SafetyObjectManager.filteredVirtualSmartags.get(i);
            if (smartag.isSent()) {
                continue;
            }
            String postData = getJsonPostData(localMacAddress, SafetyObjectManager.getWorkerCardId(smartag), smartag.issueMessage);
            if (addAlert(postData)) {
                SafetyObjectManager.filteredVirtualSmartags.get(i).setSent(true);
            }
        }

    }

    private static String getJsonPostData(String deviceId, String workerId, String issueMessage) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("deviceId", deviceId);
            postData.put("workerId", workerId);
            postData.put("issueMessage", issueMessage);
            postData.put("time", Utils.getCurrentDatetime());
        } catch (JSONException e) {
            Log.e(tag, "getJsonPostData: "+e.toString());
        }
        return postData.toString();
    }

    public String getWorkerLastUpdated() {
        return workerLastUpdated;
    }
}
