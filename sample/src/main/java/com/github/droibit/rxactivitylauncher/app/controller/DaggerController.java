package com.github.droibit.rxactivitylauncher.app.controller;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.github.droibit.rxactivitylauncher.ActivityResult;
import com.github.droibit.rxactivitylauncher.RxLauncher;
import com.github.droibit.rxactivitylauncher.app.DetailActivity;
import com.github.droibit.rxactivitylauncher.app.dagger.PerActivity;

import javax.inject.Inject;

import rx.functions.Action1;

/**
 * @author kumagai
 */
@PerActivity
public class DaggerController {

    private final Context mContext;
    private final RxLauncher mLauncher;

    @Inject
    public DaggerController(RxLauncher launcher) {
        mContext = launcher.getContext();
        mLauncher = launcher;
    }

    public void startDetailActivity() {
        final Intent intent = DetailActivity.launchIntent(mContext);
        mLauncher.startActivityForResult(intent, DetailActivity.REQUEST_DETAIL)
                 .subscribe(new Action1<ActivityResult>() {
                     @Override public void call(ActivityResult result) {
                         final String msg = result.isOk() ? "OK" : "Canceled";
                         Toast.makeText(mContext, "Received: " + msg, Toast.LENGTH_SHORT).show();
                     }
                 }) ;
    }
}
