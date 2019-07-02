package com.asa.utils.object;

import org.json.JSONException;

public abstract class ApiObject {
    protected String tag;
    public static String apiObjectName;

    public abstract ApiObject getObjectFromJson(String json) throws JSONException;

    public ApiObject(String apiObjectName) {
        ApiObject.apiObjectName = apiObjectName;
        tag = apiObjectName;
    }

    public static String getApiObjectName() {
        return apiObjectName;
    }
}
