package com.github.droibit.rxactivitylauncher;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;

/**
 * Interface to delegate the launch of the activity.
 *
 * @author kumagai
 */
public interface LaunchActivitySource {

    /**
     * Specify the Observable that trigger.
     *
     * @param trigger Observable that triggers the {@link #startActivityForResult(Intent, int, Bundle)}.
     */
    @CheckResult
    LaunchActivitySource on(@NonNull Observable<?> trigger);

    /**
     * Launch an activity for which you would like a result when it finished.
     */
    @CheckResult
    Observable<ActivityResult> startActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle options);
}
