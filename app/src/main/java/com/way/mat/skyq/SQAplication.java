package com.way.mat.skyq;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

import com.pixplicity.easyprefs.library.Prefs;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by oleh on 01.09.16.
 */
public class SQAplication extends Application {
    private static Context context;

    public void onCreate(){
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        context = getApplicationContext();


        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(context)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }

    public static Context getAppContext() {
        return SQAplication.context;
    }

}
