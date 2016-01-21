package com.github.droibit.rxactivitylauncher.app.controller;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.github.droibit.rxactivitylauncher.ActivityResult;
import com.github.droibit.rxactivitylauncher.RxLauncher;
import com.github.droibit.rxactivitylauncher.app.DetailActivity;
import com.github.droibit.rxactivitylauncher.app.dagger.PerActivity;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

import static com.github.droibit.rxactivitylauncher.app.DetailActivity.*;

/**
 * @author kumagai
 */
@PerActivity
public class DaggerController {

    private final Activity mActivity;
    private final RxLauncher mLauncher;
    private final CompositeSubscription mCompositeSubscription;

    @Inject
    public DaggerController(Activity activity, RxLauncher launcher, CompositeSubscription compositeSubscription) {
        mActivity = activity;
        mLauncher = launcher;
        mCompositeSubscription = compositeSubscription;
    }

    public void startDetailActivity() {
        final Intent intent = DetailActivity.launchIntent(mActivity, true);
        mLauncher.from(mActivity).startActivityForResult(intent, REQUEST_DETAIL, null)
                 .filter(new Func1<ActivityResult, Boolean>() {
                     @Override public Boolean call(ActivityResult result) {
                         return result.isOk();
                     }
                 }).subscribe(new Action1<ActivityResult>() {
                     @Override
                     public void call(ActivityResult result) {
                         final String msg = result.data.getStringExtra(KEY_RESPONSE_MSG);
                         Toast.makeText(mActivity, "Received: " + msg, Toast.LENGTH_SHORT).show();
                     }
                 }) ;
    }

    public void destroy() {

    }
}
