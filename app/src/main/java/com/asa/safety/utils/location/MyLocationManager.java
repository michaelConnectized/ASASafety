package com.asa.safety.utils.location;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.asa.safety.R;
import com.asa.safety.utils.Converter;

import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class MyLocationManager {
    //Strings to register to create intent filter for registering the recivers
    private static final String ACTION_STRING_SERVICE = "ToService";
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";

    private final String tag = "MyLocationManager";
    private Activity activity;

    private boolean serviceStarted = false;

    public GpsBackgroundService gpsService;
    private Location mLocation;
    private android.location.LocationManager lm;

    private long lastLocUpdateTime = new Date().getTime();

    //STEP1: Create a broadcast receiver
    public BroadcastReceiver activityReceiver = null;

    public MyLocationManager(Activity activity) {
        this.activity = activity;
    }

    //send broadcast from activity to all receivers listening to the action "ACTION_STRING_SERVICE"
    public void sendBroadcast() {
        Intent new_intent = new Intent();
        new_intent.setAction(ACTION_STRING_SERVICE);
        activity.sendBroadcast(new_intent);
    }

    public void enableGpsService() {
        final Intent gpsIntent = new Intent(activity, GpsBackgroundService.class);
        if (Build.VERSION.SDK_INT >= 26) {
            activity.startForegroundService(gpsIntent);
        } else {
            activity.startService(gpsIntent);
        }
        activity.bindService(gpsIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        //STEP1: Create a broadcast receiver
        if (gpsService!=null) {
            gpsService.startTracking();
            activityReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    long currentTime = new Date().getTime();
                    //Log.d("Testttty", String.valueOf(currentTime-lastLocUpdateTime));
                    lastLocUpdateTime = currentTime;
                    if (gpsService != null)
                        mLocation = gpsService.getmLastLocation();
                    serviceSettingFor8();

                }
            };
        }

        //STEP2: register the receiver
        if (activityReceiver != null) {
            //Create an intent filter to listen to the broadcast sent with the action "ACTION_STRING_ACTIVITY"
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_ACTIVITY);
            //Map the intent filter to the receiver
            activity.registerReceiver(activityReceiver, intentFilter);
        }
    }

    public void serviceSettingFor8(){
        if (serviceStarted)
            return;

        if (gpsService==null)
            return;

        if (Build.VERSION.SDK_INT >= 26) {
            gpsService.startForeground(1, getNoti());
        }
        serviceStarted = true;
    }

    private Notification getNoti() {
        if (Build.VERSION.SDK_INT >= 26) {
            String NOTIFICATION_CHANNEL_ID = "com.asa.safety";
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(activity, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setContentTitle("GPS")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            return notification;
        }
        return null;
    }

    public long getLastLocUpdateTime() {
        return lastLocUpdateTime;
    }

    public boolean getIsGpsSigFound() {
        Log.e(tag, "Location Time "+(new Date().getTime()-lastLocUpdateTime));
        return new Date().getTime()-lastLocUpdateTime<=30000;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();
            if (name.endsWith("GpsBackgroundService")) {
                gpsService = ((GpsBackgroundService.LocationServiceBinder) service).getService();
                serviceSettingFor8();
                mLocation = gpsService.getmLastLocation();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            if (className.getClassName().equals("BackgroundService")) {
                gpsService = null;
            }
        }
    };

    public boolean isGpsAble(){
        lm = (android.location.LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    public void stopGpsService() {
        activity.unbindService(serviceConnection);
        gpsService.stopTracking();
    }

    public Location getLocation() {
        return mLocation;
    }

    public boolean isInsideGeofencing() {
        if (mLocation!=null) {
            boolean isInside = false;
            double lat = mLocation.getLatitude();
            double lon = mLocation.getLongitude();
            Coordinate currentLoc = new Coordinate(lon, lat);

            String geofencingJson = activity.getSharedPreferences(activity.getResources().getString(R.string.share_preference), MODE_PRIVATE).getString("ProjectGeoFencing", "");

            GeoFencing gf = Converter.jsonToGeofencing(geofencingJson);

            if (gf!=null) {
                isInside = gf.isInside(currentLoc);
            }
            return isInside;
        } else
            return false;
    }
}
