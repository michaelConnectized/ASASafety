package com.asa.safety.safety.macAddress;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.asa.safety.utils.permission.Permission;
import com.asa.safety.utils.Utils;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class MacAddressManager {
    Activity activity;

    public MacAddressManager(Activity activity) {
        this.activity = activity;
    }

    public boolean saveMacAddr() {
        boolean result = false;
        getPermission();
        String macAddress = getMacAddress();

        if (!macAddress.equals("")) {
            saveMacAddrInSharePreference(macAddress);
            result = true;
        }
        return result;
    }

    private void getPermission() {
        if (!isPermissionGranted()) {
            Permission permission = new Permission(activity);
            permission.checkPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
        }
    }

    private boolean isPermissionGranted() {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    private void saveMacAddrInSharePreference(String mac) {
        SharedPreferences sp = Utils.getSharePreference(activity);
        sp.edit().putString("mac", mac).commit();
    }

    private static String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString().replace(":", "").toLowerCase();
            }
        } catch (Exception ex) {
        }
        return "";
    }
}
