package com.asa.safety.utils.location;

import org.json.JSONArray;

public class Coordinate {
    private Double x;
    private Double y;

    public Coordinate(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public String toJson() {
        JSONArray tmpArray = new JSONArray();
        tmpArray.put(x);
        tmpArray.put(y);
        return tmpArray.toString();
    }
}
