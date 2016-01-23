package com.github.droibit.rxactivitylauncher.app;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * @author kumagai
 */
public class MyApplication extends Application {

    /** {@inheritDoc} */
    @Override
    public void onCreate() {
        super.onCreate();

        LeakCanary.install(this);
    }
}
