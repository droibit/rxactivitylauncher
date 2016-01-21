package com.github.droibit.rxactivitylauncher.app.dagger;

import android.app.Activity;

import com.github.droibit.rxactivitylauncher.RxLauncher;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.subscriptions.CompositeSubscription;

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
    CompositeSubscription provideCompositeSubscription() {
        return new CompositeSubscription();
    }

    @Provides
    @PerActivity
    RxLauncher provideRxLauncher() {
        return RxLauncher.getInstance();
    }
}
