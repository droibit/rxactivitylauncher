package com.github.droibit.rxactivitylauncher.app.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.github.droibit.rxactivitylauncher.ActivityResult;
import com.github.droibit.rxactivitylauncher.RxLauncher;
import com.github.droibit.rxactivitylauncher.app.DetailActivity;
import com.github.droibit.rxactivitylauncher.app.dagger.PerActivity;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.functions.Func1;

import static com.github.droibit.rxactivitylauncher.app.DetailActivity.*;

/**
 * @author kumagai
 */
@PerActivity
public class DaggerController {

    private final Context mContext;
    private final RxLauncher mLauncher;

    @Inject
    public DaggerController(Activity activity, RxLauncher launcher) {
        mContext = activity;
        mLauncher = launcher;
    }

    public void startDetailActivity() {
        final Intent intent = DetailActivity.launchIntent(mContext, true);
        mLauncher.startActivityForResult(intent, REQUEST_DETAIL)
                 .filter(new Func1<ActivityResult, Boolean>() {
                     @Override public Boolean call(ActivityResult result) {
                         return result.isOk();
                     }
                 }).subscribe(new Action1<ActivityResult>() {
                     @Override
                     public void call(ActivityResult result) {
                         final String msg = result.data.getStringExtra(KEY_RESPONSE_MSG);
                         Toast.makeText(mContext, "Received: " + msg, Toast.LENGTH_SHORT).show();
                     }
                 }) ;
    }
}
