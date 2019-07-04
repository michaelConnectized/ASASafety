package com.asa.safety.utils.location;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;

public class GeoFencing {
    private ArrayList<Coordinate> pairs;

    public GeoFencing() {
        pairs = new ArrayList<Coordinate>();
    }

    public Boolean isInside(Coordinate yrCo) {
        ArrayList<Double> polygonXs = new ArrayList<>();
        Boolean inside = false;
        Double x;

        //cut of the x lines
        for (int i=0; i<pairs.size(); i++) {
            x = getX(yrCo.getY(), pairs.get(i), pairs.get((i+1)%pairs.size()));
            if (x != null) {
                polygonXs.add(x);
            }
        }
        Collections.sort(polygonXs);

        Boolean linePairTrigger = false;
        for (int i=0; i<polygonXs.size(); i++) {

            if (!linePairTrigger) {
                if (yrCo.getX() > polygonXs.get(i)) {
                    inside = true;
                    linePairTrigger = true;
                }
            } else {
                if (yrCo.getX() > polygonXs.get(i)) {
                    inside = false;
                    linePairTrigger = false;
                }
            }
        }

        return inside;
    }

    public Double getX(Double y, Coordinate co1, Coordinate co2) {
        Double x;
        Double slope = (co2.getY() - co1.getY())/(co2.getX() - co1.getX());
        x = (slope*co1.getX() - co1.getY() + y)/slope;

        Double max = co1.getX();
        Double min = co2.getX();
        if (co2.getX() > max) {
            max = co2.getX();
            min = co1.getX();
        }
        System.out.println("Max: "+ String.valueOf(max)+", Min: "+ String.valueOf(min)+", X: "+ String.valueOf(x)+", Slope: "+ String.valueOf(slope));
        if ((x > max) |(x < min)) {
            x = null;
        }

        return x;
    }

    public void addPair(Coordinate co) {
        pairs.add(co);
    }

    public String toJson() {
        try {
            JSONArray tmpArray = new JSONArray();
            for (int i=0; i<pairs.size(); i++) {
                tmpArray.put(new JSONArray(pairs.get(i).toJson()));
            }
            return tmpArray.toString();
        } catch (Exception e) {
            return "error";
        }
    }
}
