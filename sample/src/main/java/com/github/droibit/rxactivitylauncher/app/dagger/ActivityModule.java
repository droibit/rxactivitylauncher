package com.github.droibit.rxactivitylauncher.app.dagger;

import android.app.Activity;

import com.github.droibit.rxactivitylauncher.RxLauncher;
import com.github.droibit.rxactivitylauncher.app.controller.DaggerController;

import dagger.Module;
import dagger.Provides;

/**
 * @author kumagai
 */
@Module
public class ActivityModule {

    private final Activity mActivity;

    public ActivityModule(Activity activity) {
        this.mActivity = activity;
    }

    @Provides
    @PerActivity
    Activity activity() {
        return mActivity;
    }

    @Provides
    @PerActivity
    RxLauncher provideRxLauncher() {
        return RxLauncher.from(mActivity);
    }
}
