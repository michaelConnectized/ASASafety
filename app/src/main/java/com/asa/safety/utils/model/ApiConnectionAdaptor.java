package com.asa.safety.utils.model;

import android.content.res.Resources;
import android.util.Log;

import com.asa.safety.R;
import com.asa.safety.utils.object.ApiObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApiConnectionAdaptor {
    private static final String tag = "ApiConnectionAdaptor";

    protected Resources res;
    private boolean isHttps;

    public ApiConnectionAdaptor(Resources res) {
        this.res = res;
        isHttps = res.getBoolean(R.bool.is_https);
    }

    protected List<ApiObject> tryJsonToApiObjectList(String apiJsonName, String json, ApiObject objClass) {
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

    protected boolean isSuccess(String json) {
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

    protected String tryExecuteAndGetFromServer(String fullUrl, String postData) {
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
}
