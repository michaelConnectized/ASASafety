package com.asa.asasafety.Model;

import android.app.Activity;
import android.content.res.Resources;

import com.asa.asasafety.R;

public class ApiConnectionAdaptor {
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

    public String getDangerZoneList(String postData) {
        return tryExecuteAndGetFromServer(getDangerZoneListUrl, postData);
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
