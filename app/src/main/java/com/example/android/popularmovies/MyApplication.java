package com.example.android.popularmovies;

import android.app.Application;

import com.example.android.popularmovies.Utils.ConnectivityReceiver;


/**
 * ConnectivityReceiver, MyApplication classes written based on androidhive tutorial
 * that was suggested by a project reviewer
 *
 * https://www.androidhive.info/2012/07/android-detect-internet-connection-status/
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }

    public static synchronized MyApplication getInstance (){
        return instance;
    }

    public void setConnectivityListener (ConnectivityReceiver.ConnectivityReceiverListener listener){
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
