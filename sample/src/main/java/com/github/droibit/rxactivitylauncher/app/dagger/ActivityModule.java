package com.github.droibit.rxactivitylauncher.app.dagger;

import android.app.Activity;

import com.github.droibit.rxactivitylauncher.RxActivityLauncher;

import dagger.Module;
import dagger.Provides;
import rx.subscriptions.CompositeSubscription;

/**
 * @author kumagai
 */
@Module
public class ActivityModule {

    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    Activity activity() {
        return activity;
    }

    @Provides
    @PerActivity
    RxActivityLauncher provideRxLauncher() {
        return new RxActivityLauncher();
    }
}
