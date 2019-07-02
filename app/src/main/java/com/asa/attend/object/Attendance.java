package com.asa.attend.object;

import com.asa.utils.object.ApiObject;

import org.json.JSONException;
import org.json.JSONObject;

public class Attendance extends ApiObject {
    private String hubID;
    private String helmetID;
    private String txPwr;
    private String rssi;
    private String dateTime;
    private Double lon;
    private Double lat;
    private String source;
    private Double errorRate;
    private Double battery;

    public Attendance getObjectFromJson(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        Attendance resultAttendance = new Attendance();

        resultAttendance.setHubID(jsonObject.getString("hubId"));
        resultAttendance.setHelmetID(jsonObject.getString("helmetID"));
        resultAttendance.setTxPwr(jsonObject.getString("txPwr"));
        resultAttendance.setRssi(jsonObject.getString("rssi"));
        resultAttendance.setDateTime(jsonObject.getString("dateTime"));
        resultAttendance.setLon(jsonObject.getDouble("lon"));
        resultAttendance.setLat(jsonObject.getDouble("lat"));
        resultAttendance.setSource(jsonObject.getString("source"));
        resultAttendance.setErrorRate(jsonObject.getDouble("errorRate"));
        resultAttendance.setBattery(jsonObject.getDouble("battery"));
        return resultAttendance;
    }

    public Attendance() {
        super("attendanceList");
    }

    public String getHubID() {
        return hubID;
    }

    public void setHubID(String hubID) {
        this.hubID = hubID;
    }

    public String getHelmetID() {
        return helmetID;
    }

    public void setHelmetID(String helmetID) {
        this.helmetID = helmetID;
    }

    public String getTxPwr() {
        return txPwr;
    }

    public void setTxPwr(String txPwr) {
        this.txPwr = txPwr;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Double getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(Double errorRate) {
        this.errorRate = errorRate;
    }

    public Double getBattery() {
        return battery;
    }

    public void setBattery(Double battery) {
        this.battery = battery;
    }

    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("hubID", hubID);
            jsonObject.put("helmetID", helmetID);
            jsonObject.put("txPwr", txPwr);
            jsonObject.put("rssi", rssi);
            jsonObject.put("dateTime", dateTime);
            jsonObject.put("lon", lon);
            jsonObject.put("lat", lat);
            jsonObject.put("source", source);
            jsonObject.put("errorRate", errorRate);
            jsonObject.put("battery", battery);
        } catch (JSONException e) {
            return "";
        }
        return jsonObject.toString();
    }

    @Override
    public boolean equals(Object obj) {
        boolean retVal = false;

        if (obj instanceof Attendance){
            Attendance attendance = (Attendance) obj;
            retVal = attendance.helmetID.equals(this.helmetID) && attendance.dateTime.equals(this.dateTime);
        }

        return retVal;
    }
}
