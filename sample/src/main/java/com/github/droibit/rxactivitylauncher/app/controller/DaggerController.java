package com.github.droibit.rxactivitylauncher.app.controller;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.github.droibit.rxactivitylauncher.ActivityResult;
import com.github.droibit.rxactivitylauncher.RxActivityLauncher;
import com.github.droibit.rxactivitylauncher.app.DetailActivity;
import com.github.droibit.rxactivitylauncher.app.dagger.PerActivity;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

import static com.github.droibit.rxactivitylauncher.app.DetailActivity.*;

/**
 * @author kumagai
 */
@PerActivity
public class DaggerController {

    private final Activity activity;

    private final RxActivityLauncher activityLauncher;

    @Inject
    public DaggerController(Activity activity, RxActivityLauncher launcher) {
        this.activity = activity;
        this.activityLauncher = launcher;
    }

    public void startDetailActivity() {
        final Intent intent = DetailActivity.launchIntent(activity, true);
        activityLauncher.from(activity)
                .startActivityForResult(intent, REQUEST_DETAIL, null)
                .filter(new Func1<ActivityResult, Boolean>() {
                    @Override
                    public Boolean call(ActivityResult result) {
                        return result.isOk();
                    }
                }).subscribe(new Action1<ActivityResult>() {
                    @Override
                    public void call(ActivityResult result) {
                        @SuppressWarnings("ConstantConditions")
                        final String msg = result.data.getStringExtra(KEY_RESPONSE_MSG);
                        Toast.makeText(activity, "Received: " + msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
