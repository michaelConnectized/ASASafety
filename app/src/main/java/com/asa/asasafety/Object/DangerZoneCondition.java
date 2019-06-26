package com.asa.asasafety.Object;

import org.json.JSONException;
import org.json.JSONObject;

public class DangerZoneCondition extends ApiObject {
    private String hubid;
    private String op;
    private int rssi;

    public DangerZoneCondition getObjectFromJson(String json) throws JSONException {
        DangerZoneCondition resultDangerZoneCondition = new DangerZoneCondition();
        JSONObject jsonObject = new JSONObject(json);
        resultDangerZoneCondition.setHubid(jsonObject.getString("hubId"));
        resultDangerZoneCondition.setOp(jsonObject.getString("op"));
        resultDangerZoneCondition.setRssi(jsonObject.getInt("rssi"));
        return resultDangerZoneCondition;
    }

    public DangerZoneCondition() {
        super("Condition");
    }

    public String getHubid() {
        return hubid;
    }

    public String getOp() {
        return op;
    }

    public int getRssi() {
        return rssi;
    }

    public void setHubid(String hubid) {
        this.hubid = hubid;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("hubid", hubid);
            jsonObject.put("op", op);
            jsonObject.put("rssi", rssi);
        } catch (JSONException e) {
            return "";
        }
        return jsonObject.toString();
    }
}
