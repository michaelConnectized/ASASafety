package com.asa.utils;

import com.asa.utils.location.Coordinate;
import com.asa.utils.location.GeoFencing;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class Converter {

    public static GeoFencing jsonToGeofencing(String json) {
        GeoFencing tmpGeoFencing = new GeoFencing();
        try {
            JSONArray tmpArray = new JSONArray(json);
            for (int i=0; i<tmpArray.length(); i++) {
                Coordinate tmpCoo = new Coordinate(tmpArray.getJSONArray(i).getDouble(0), tmpArray.getJSONArray(i).getDouble(1));
                tmpGeoFencing.addPair(tmpCoo);
            }
            return tmpGeoFencing;
        } catch (Exception e) {
            return null;
        }
    }
}
