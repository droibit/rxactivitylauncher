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
public abstract class Launchable {

    /**
     * Launch an activity for which you would like a result when it finished.
     **/
    @CheckResult
    public abstract Observable<ActivityResult> startActivityForResult(@NonNull Intent intent,
                                                                      int requestCode,
                                                                      @Nullable Bundle options);

    /**
     * Launch an activity for which you would like a result when it finished.
     * <p>
     * After other activity launched, you use this method if the screen might rotate.
     * </p>
     */
    @CheckResult
    public abstract Observable<ActivityResult> startActivityForResult(@Nullable Observable<?> trigger,
                                                                      @NonNull Intent intent,
                                                                      int requestCode,
                                                                      @Nullable Bundle options);

    protected abstract void startActivity(@NonNull Intent intent,
                                          int requestCode,
                                          @Nullable Bundle options);
}
