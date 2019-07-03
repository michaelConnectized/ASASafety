package com.asa.safety.model;

import android.content.res.Resources;
import android.util.Log;

import com.asa.safety.object.Alert;
import com.asa.utils.model.ApiConnectionAdaptor;
import com.asa.utils.object.ApiObject;
import com.asa.safety.object.DangerZone;
import com.asa.safety.object.VirtualSmartag;
import com.asa.safety.object.Worker;
import com.asa.safety.objectManager.SafetyObjectManager;
import com.asa.safety.R;
import com.asa.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SafetyApiConnectionAdaptor extends ApiConnectionAdaptor {
    private static final String tag = "SafetyApiConnectionAdaptor";

    private String baseUrl;
    private String getDangerZoneListUrl;
    private String getCurrentWorkerListUrl;
    private String addAlertUrl;
    private String getAlertsUrl;

    private String workerLastUpdated = "N/A";

    public SafetyApiConnectionAdaptor(Resources res) {
        super(res);
        initVar();
    }

    private void initVar() {
        baseUrl = res.getString(R.string.api_safety_base_url);
        getDangerZoneListUrl = baseUrl + res.getString(R.string.get_danger_zone_list_url);
        getCurrentWorkerListUrl = baseUrl + res.getString(R.string.get_current_worker_list_url);
        addAlertUrl = baseUrl + res.getString(R.string.add_alert_url);
        getAlertsUrl = baseUrl + res.getString(R.string.get_alerts_url);
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
