package com.example.android.popularmovies;

import android.app.Application;


/**
 * ConnectivityReceiver, MyApplication classes written based on androidhive tutorial
 * that was suggested by a project reviewer
 *
 * https://www.androidhive.info/2012/07/android-detect-internet-connection-status/
 */

public class MyApplication extends Application {

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}