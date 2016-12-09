package com.github.droibit.rxactivitylauncher2;


import com.github.droibit.rxactivitylauncher.ActivityResult;

import android.support.annotation.CheckResult;

import rx.Observable;

/**
 * Interface to delegate the launch of the activity.<br/>
 */
public interface PendingLaunchActivitySource {

    /**
     * Launch an activity for which you would like a result when it finished.
     */
    @CheckResult
    Observable<ActivityResult> startActivityForResult(int requestCode);
}
