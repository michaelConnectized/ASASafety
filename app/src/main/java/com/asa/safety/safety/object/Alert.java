package com.asa.safety.safety.object;

import com.asa.safety.utils.object.ApiObject;

import org.json.JSONException;
import org.json.JSONObject;

public class Alert extends ApiObject {
    private String deviceId;
    private String workerId;
    private String issueMessage;
    private String time;

    public Alert getObjectFromJson(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        Alert resultAlert = new Alert();
        resultAlert.setDeviceId(jsonObject.getString("deviceId"));
        resultAlert.setWorkerId(jsonObject.getString("workerId"));
        resultAlert.setIssueMessage(jsonObject.getString("issueMessage"));
        resultAlert.setTime(jsonObject.getString("time"));
        return resultAlert;
    }

    public Alert() {
        super("Alert");
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getIssueMessage() {
        return issueMessage;
    }

    public void setIssueMessage(String issueMessage) {
        this.issueMessage = issueMessage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceId", deviceId);
            jsonObject.put("workerId", workerId);
            jsonObject.put("issueMessage", issueMessage);
            jsonObject.put("time", time);
        } catch (JSONException e) {
            return "";
        }
        return jsonObject.toString();
    }
}
