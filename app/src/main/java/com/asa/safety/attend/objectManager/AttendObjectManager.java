package com.asa.safety.attend.objectManager;

import android.app.Activity;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import com.asa.safety.attend.analyser.BeaconAnalyser;
import com.asa.safety.attend.object.Attendance;
import com.asa.safety.utils.location.MyLocationManager;
import com.moko.support.entity.DeviceInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AttendObjectManager {
    private final static String tag = "AttendObjectManager";

    public static List<Attendance> filteredAttendanceList;
    private static MyLocationManager myLocationManager;

    public static void initLocationManager(Activity activity) {
        myLocationManager = new MyLocationManager(activity);
    }

    public static void checkAndInitList() {
        if (filteredAttendanceList==null) { filteredAttendanceList = new ArrayList<>(); }
    }

    public static boolean isLocationSet() {
        return (myLocationManager!=null) && (myLocationManager.getLocation()!=null);
    }

    public static void checkAndAddFilteredAttendanceList(DeviceInfo deviceInfo, String localMacAddress) throws NullPointerException {
        int helmetVersionNumber = getHelmetVersionNumber(deviceInfo);
        if (isHelmet(helmetVersionNumber)) {
            int txPwr = getTxPower(deviceInfo, helmetVersionNumber);
            if (txPwr!=0)
                filteredAttendanceList.add(deviceInfoToAttendance(deviceInfo, txPwr, myLocationManager.getLocation(), localMacAddress));
        }
    }

    private static int getHelmetVersionNumber(DeviceInfo deviceInfo) {
        int versionNumebr = -1;
        boolean isHelmetVersion3;
        boolean isHelmetVersion2;
        boolean isHelmetVersion1;

        BeaconAnalyser beaconAnalyser = new BeaconAnalyser(deviceInfo.scanRecord.getBytes());

        isHelmetVersion3 = beaconAnalyser.getDeviceCode()==BeaconAnalyser.HELMETV3;
        isHelmetVersion2 = (beaconAnalyser.getDeviceCode()==BeaconAnalyser.HELMETV2) && (!beaconAnalyser.getHmV2Device().isPackage2());
        isHelmetVersion1 = beaconAnalyser.getDeviceCode()==BeaconAnalyser.HELMETV1;

        if (isHelmetVersion3) versionNumebr = 3;
        if (isHelmetVersion2) versionNumebr = 2;
        if (isHelmetVersion1) versionNumebr = 1;

        return versionNumebr;
    }

    private static boolean isHelmet(int versionNumber) {
        return versionNumber>0;
    }

    private static Attendance deviceInfoToAttendance(DeviceInfo deviceInfo, int txPwr, Location location, String localMacAddress) {
        Attendance attendance = new Attendance();
        attendance.setRssi(String.valueOf(deviceInfo.rssi));

        attendance.setHubID( localMacAddress );
        attendance.setHelmetID( deviceInfo.getMacShortForm() );
        attendance.setTxPwr( String.valueOf( txPwr ) );
        attendance.setRssi(String.valueOf( deviceInfo.rssi ) );
        if (Build.VERSION.SDK_INT >= 24 )
            attendance.setDateTime( new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date()));
        else
            attendance.setDateTime( new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ").format(new Date()) );
        attendance.setLon( location.getLongitude() );
        attendance.setLat( location.getLatitude() );
        attendance.setSource( location.getProvider().equals("gps")? "GPS":"Network" );
        attendance.setErrorRate(Double.valueOf(location.getAccuracy()));

        return attendance;
    }

    private static int getTxPower(DeviceInfo deviceInfo, int helmetVersionNumber) {
        int txPwr = -999;
        try {
            BeaconAnalyser beaconAnalyser = new BeaconAnalyser(deviceInfo.scanRecord.getBytes());

            switch (helmetVersionNumber) {
                case 1: txPwr = beaconAnalyser.getaIbeacon().getTxpower(); break;
                case 2: txPwr = beaconAnalyser.getHmV2Device().getTxPower(); break;
                case 3: txPwr = beaconAnalyser.getHmV3Device().getTxPowers().get(0); break;
            }
        } catch (Exception e) {
            Log.e(tag, e.toString());
        }

        return txPwr;
    }

    public static MyLocationManager getMyLocationManager() {
        return myLocationManager;
    }
}
