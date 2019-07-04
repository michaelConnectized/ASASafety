package com.asa.safety.safety.object;

import com.asa.safety.utils.object.ApiObject;
import com.asa.safety.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DangerZone extends ApiObject {
    private String id;
    private String name;
    private List<DangerZoneCondition> conditions;
    private List<String> disallowTradeCodes;
    private List<String> disallowWorkerCardIds;
    private String lastUpdated;

    public DangerZone getObjectFromJson(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        DangerZone resultDangerZone = new DangerZone();

        String id = jsonObject.getString("id");
        String name = jsonObject.getString("name");
        List<DangerZoneCondition> conditions = new ArrayList<>();

        JSONArray conditionsJsonArray = jsonObject.getJSONArray("conditions");
        for (int i=0; i<conditionsJsonArray.length(); i++) {
            conditions.add(new DangerZoneCondition().getObjectFromJson(conditionsJsonArray.get(i).toString()));
        }

        List<String> disallowTradeCodes = Utils.jsonArrayToStringArrayList(jsonObject.getJSONArray("disallowTradeCodes"));
        List<String> disallowWorkerCardIds = Utils.jsonArrayToStringArrayList(jsonObject.getJSONArray("disallowWorkerCardIds"));
        String lastUpdated = jsonObject.getString("lastUpdated");


        resultDangerZone.setId(id);
        resultDangerZone.setName(name);
        resultDangerZone.setConditions(conditions);
        resultDangerZone.setDisallowTradeCodes(disallowTradeCodes);
        resultDangerZone.setDisallowWorkerCardIds(disallowWorkerCardIds);
        resultDangerZone.setLastUpdated(lastUpdated);
        return resultDangerZone;
    }

    public DangerZone() {
        super("dangerZones");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<DangerZoneCondition> getConditions() {
        return conditions;
    }

    public List<String> getDisallowTradeCodes() {
        return disallowTradeCodes;
    }

    public List<String> getDisallowWorkerCardIds() {
        return disallowWorkerCardIds;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConditions(List<DangerZoneCondition> conditions) {
        this.conditions = conditions;
    }

    public void setDisallowTradeCodes(List<String> disallowTradeCodes) {
        this.disallowTradeCodes = disallowTradeCodes;
    }

    public void setDisallowWorkerCardIds(List<String> disallowWorkerCardIds) {
        this.disallowWorkerCardIds = disallowWorkerCardIds;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("name", name);
            jsonObject.put("conditions", conditions);
            jsonObject.put("disallowTradeCodes", disallowTradeCodes);
            jsonObject.put("disallowWorkerCardIds", disallowWorkerCardIds);
            jsonObject.put("lastUpdated", lastUpdated);
        } catch (JSONException e) {
            return "";
        }
        return jsonObject.toString();
    }
}
