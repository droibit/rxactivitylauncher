package com.github.droibit.rxactivitylauncher;


import android.support.annotation.CheckResult;

import rx.Observable;

/**
 * Interface to delegate the launch of the activity.<br/>
 *
 * @author kumagai
 */
public interface PendingLaunchActivitySource {

    /**
     * Launch an activity for which you would like a result when it finished.
     */
    @CheckResult
    Observable<ActivityResult> startActivityForResult(int requestCode);
}
