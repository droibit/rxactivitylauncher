package com.github.droibit.rxactivitylauncher2;


import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import io.reactivex.Observable;


/**
 * Interface to delegate the launch of the activity.
 */
public interface PendingLaunchActivitySource {

    /**
     * Launch an activity for which you would like a result when it finished.
     */
    @NonNull
    @CheckResult
    Observable<ActivityResult> startActivityForResult(int requestCode);
}
