package edu.umich.dstudio.prompt;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import com.firebase.client.Firebase;
import com.google.android.gms.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import edu.umich.dstudio.R;
import edu.umich.dstudio.model.FirebaseWrapper;
import edu.umich.dstudio.model.LastLocation;
import edu.umich.dstudio.ui.MainActivity;
import edu.umich.dstudio.utils.Constants;
import edu.umich.dstudio.utils.GSharedPreferences;
import edu.umich.dstudio.utils.NotificationUtils;
import edu.umich.dstudio.utils.Utils;

/**
 * Created by neera_000 on 3/27/2016.
 *
 * This service runs every @link{Constants.PROMPT_SERVICE_REPEAT_MILLISECONDS} to upload the client's
 * current location as well as to check if any notifications need to be shown to the client.
 */
public class PromptService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final String LOG_TAG = PromptService.class.getSimpleName();
    private boolean mCurrentlyProcessingLocation = false;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_TAG, "Running prompt service daemon...");
        if (!mCurrentlyProcessingLocation) {
            mCurrentlyProcessingLocation = true;
            attemptToGetLocation();
        }
        PromptConfig pendingNotificationConfig = Utils.getPromptConfigForPendingNotification();
        if (GSharedPreferences.getInstance().getPreference(Constants.CAN_SHOW_NOTIFICATION).equals(Constants.YES)
                &&  pendingNotificationConfig != null) {
            createNotification(pendingNotificationConfig);
        }
        return START_NOT_STICKY;
    }

    private void attemptToGetLocation() {
        Log.d(LOG_TAG, "Attempting to get location");
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            Log.e(LOG_TAG, "Error occurred while attempting to access Google play.");
        }

    }

    private void stopCheckingForLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            stopSelf();
        }
    }

    private void createNotification(PromptConfig pendingNotificationConfig) {
        Notification n = NotificationUtils.createNotification(this,
                getPackageManager(), pendingNotificationConfig);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, n);
    }

    // All listeners after this point.
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Destroying service.");
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + Constants.PROMPT_SERVICE_REPEAT_MILLISECONDS,
                PendingIntent.getService(this, 0, new Intent(this, PromptService.class), 0)
        );
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(LOG_TAG, "onConnected");

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest,
                this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // no-op
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.e(LOG_TAG, "GPS: "
                    + location.getLatitude() + ", "
                    + location.getLongitude() + ", "
                    + "accuracy: " + location.getAccuracy());
            // If the location is accurate to 500 meters, it's good enough for us. Exit.
            if (location.getAccuracy() < 500.0f) {
                LastLocation l = new LastLocation((float)location.getLatitude(), (float)location.getLongitude());
                FirebaseWrapper.getInstance().uploadLastLocation(l);
                stopCheckingForLocationUpdates();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Connection to Google play services failed.");
        stopCheckingForLocationUpdates();
        stopSelf();
    }

}
