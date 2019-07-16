package com.asa.safety.utils.location;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;


public class GpsBackgroundService extends Service implements GpsStatus.Listener {
    private final LocationServiceBinder binder = new LocationServiceBinder();
    private final String TAG = "GpsBackgroundService";
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private NotificationManager notificationManager;
    private Notification.Builder mBuilder;
    private GpsStatus mStatus;

    Location mLastLocation;

    private final int LOCATION_INTERVAL = 10000; // 10 sec
    private final int LOCATION_DISTANCE = 0;
    private static final int COMPARE_TIME = 10000; // 10 sec

    @Override
    public IBinder onBind(Intent intent) {
        startTracking();
        return binder;
    }

    private class LocationListener implements android.location.LocationListener {
        private Location lastLocation;
        private final String TAG = "LocationListener";

        public LocationListener() { }

        public LocationListener(String provider) {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
//            Log.d(TAG, "LocationChange received: " + location);

            if (!isBetterLocation(location, mLastLocation))
                return;

            mLastLocation = location;
            updateLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
//            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
//            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
//            Log.e(TAG, "onStatusChanged: " + status);
        }
    }

    private void updateLocation(Location location) {
//        Log.i(TAG, "LocationChanged: " + location);
        Intent intent = new Intent();
        intent.setAction(ACTION_STRING_ACTIVITY);
        intent.putExtra("Lat", location.getLatitude());
        intent.putExtra("Lon", location.getLongitude());
        intent.putExtra("Err", location.getAccuracy());
        sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        if (serviceReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_SERVICE);
            registerReceiver(serviceReceiver, intentFilter);
        }
        initializeLocationManager();
    }

    @Override
    public void onDestroy() {
        removeLocationManager();
        super.onDestroy();
    }

    public void removeLocationManager() {
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(mLocationListener);
                mLocationManager = null;
            } catch (Exception ex) {
                Log.i(TAG, "fail to remove mLocation listners, ignore"+ ex);
            }
        }
        try {
            unregisterReceiver(serviceReceiver);
        } catch (Exception ex) {
            Log.i(TAG, "fail to remove serviceReceiver, ignore"+ ex);
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void initializeLocationManager() {
        try {
            if (mLocationManager == null) {
                mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                mLocationManager.addGpsStatusListener(this);
            }
        } catch (SecurityException e) {
            Log.e(TAG, e.toString());
        }

    }

    @SuppressWarnings({"MissingPermission"})
    public void onGpsStatusChanged(int event) {
        try {
            if (mLocationManager!=null)
                mStatus = mLocationManager.getGpsStatus(mStatus);
        } catch (SecurityException e) {
            Log.e(TAG, e.toString());
        }
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                break;

            case GpsStatus.GPS_EVENT_FIRST_FIX:
                break;

            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                break;
        }

    }

    public void startTracking() {
        Log.e(TAG, "startTracking()");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "PERMISSION NOT GRANTED!!");
            return;
        }
        initializeLocationManager();
        mLocationListener = new LocationListener();

        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener);
        } catch (java.lang.SecurityException ex) {
            Log.e(TAG, "fail to request mLocation update, ignore"+ ex);
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "GPS_PROVIDER does not exist " + ex.getMessage());
        }

        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener);
        } catch (Exception ex) {
            Log.e(TAG, "NETWORK_PROVIDER does not exist " + ex.getMessage());
        }

        if (mLastLocation == null) {
            mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if( mLastLocation != null ) {
            Log.d(TAG, "mLastLocation: " + mLastLocation.toString());
            updateLocation(mLastLocation);
        } else {
            Log.d(TAG, "mLastLocation: NULL");
        }

    }

    public void stopTracking() {
        Log.e(TAG, "stopTracking()");
        removeLocationManager();
        try {
            NotificationManager notificationManager = (NotificationManager)
                    getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        }catch ( Exception e ) {
            e.printStackTrace();
        }
        stopSelf();
        this.onDestroy();
    }


    public class LocationServiceBinder extends Binder {
        public GpsBackgroundService getService() {
            return GpsBackgroundService.this;
        }
    }


    //Strings to register to create intent filter for registering the recivers
    private static final String ACTION_STRING_SERVICE = "ToService";
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";

    //STEP1: Create a broadcast receiver
    private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "received message in service..!", Toast.LENGTH_SHORT).show();
            Log.d("Service", "Sending broadcast to activity");
            sendBroadcast();
        }
    };

    //send broadcast from activity to all receivers listening to the action "ACTION_STRING_ACTIVITY"
    private void sendBroadcast() {
        Intent new_intent = new Intent();
        new_intent.setAction(ACTION_STRING_ACTIVITY);
        sendBroadcast(new_intent);
    }

    public Location getmLastLocation() {
//        Log.e(TAG, "getmLastLocation()");
        if( mLastLocation == null )
            Log.e(TAG, "mLastLocation == null");
        return mLastLocation;
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        Log.e(TAG, "isBetterLocation");

        if (currentBestLocation == null) {
            // A new mLocation is always better than no mLocation
            return true;
        }

        // Check whether the new mLocation fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > COMPARE_TIME;
        boolean isSignificantlyOlder = timeDelta < -COMPARE_TIME;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current mLocation, use the new mLocation
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new mLocation is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new mLocation fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new mLocation are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine mLocation quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


}
