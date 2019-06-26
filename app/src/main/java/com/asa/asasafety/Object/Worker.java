package com.asa.asasafety.Object;

import org.json.JSONException;
import org.json.JSONObject;

public class Worker extends ApiObject {
    private String cardId;
    private String helmetId;
    private String name;
    private String tradeCode;
    private String checkinTime;

    public Worker getObjectFromJson(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        Worker resultWorker = new Worker();
        resultWorker.setCardId(jsonObject.getString("cardId"));
        resultWorker.setHelmetId(jsonObject.getString("helmetId"));
        resultWorker.setName(jsonObject.getString("name"));
        resultWorker.setTradeCode(jsonObject.getString("tradeCode"));
        resultWorker.setCheckinTime(jsonObject.getString("checkinTime"));
        return resultWorker;
    }

    public Worker() {
        super("Worker");
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getHelmetId() {
        return helmetId;
    }

    public void setHelmetId(String helmetId) {
        this.helmetId = helmetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTradeCode() {
        return tradeCode;
    }

    public void setTradeCode(String tradeCode) {
        this.tradeCode = tradeCode;
    }

    public String getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(String checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cardId", cardId);
            jsonObject.put("helmetId", helmetId);
            jsonObject.put("name", name);
            jsonObject.put("tradeCode", tradeCode);
            jsonObject.put("checkinTime", checkinTime);
        } catch (JSONException e) {
            return "";
        }
        return jsonObject.toString();
    }
}
