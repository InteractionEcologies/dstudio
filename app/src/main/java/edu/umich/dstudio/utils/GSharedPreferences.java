package edu.umich.dstudio.utils;

/**
 * Created by neera_000 on 3/26/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by neera_000 on 1/28/2016.
 *
 * This class can be used to store the globally shared preferences, e.g. the login details of the
 * user, firebase information etc.
 */
public class GSharedPreferences {
    private static AtomicInteger numActivities = new AtomicInteger(0);
    private String LOG_TAG = getClass().getSimpleName();
    private static GSharedPreferences mInstance;
    private Context mContext;
    //
    private SharedPreferences mMyPreferences;

    private GSharedPreferences(){ }

    public static GSharedPreferences getInstance(){
        if (mInstance == null) mInstance = new GSharedPreferences();
        return mInstance;
    }

    public void Initialize(Context ctxt){
        mContext = ctxt;
        //
        mMyPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public void writePreference(String key, String value){
        SharedPreferences.Editor e = mMyPreferences.edit();
        e.putString(key, value);
        e.commit();
    }

    public String getPreference(String key) {
        return mMyPreferences.getString(key, null);
    }

    public Set<String> getHashSetPreference(String key) {
        return mMyPreferences.getStringSet(key, new HashSet<String>());
    }

    public void clear() {
        SharedPreferences.Editor e = mMyPreferences.edit();
        e.clear();
        e.commit();
    }

}
