package com.asa.utils.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class Permission {
    private final static int MY_PERMISSION = 210;

    private final String tag = "Permission";
    private Activity activity;

    public Permission(Activity activity) {
        this.activity = activity;
    }

    public boolean checkPermission(String[] permissionTypes) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean permissionExisted = true;
                for (int i=0; i<permissionTypes.length; i++) {
                    if (ActivityCompat.checkSelfPermission(activity, permissionTypes[i]) != PackageManager.PERMISSION_GRANTED) {
                        permissionExisted = false;
                    }
                }
                if (!permissionExisted) {
                    ActivityCompat.requestPermissions(activity, permissionTypes, MY_PERMISSION);
                    return false;
                }
                return true;
            } else {
                return true;
            }
        } catch (Exception e) {
            Log.e(tag, e.toString());
            return false;
        }
    }
}
