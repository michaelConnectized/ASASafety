package com.asa.asasafety.Object;

public class ApiObject {
    protected String tag;
    public static String apiObjectName;

    public ApiObject(String apiObjectName) {
        ApiObject.apiObjectName = apiObjectName;
        tag = apiObjectName;
    }

    public static String getApiObjectName() {
        return apiObjectName;
    }
}
