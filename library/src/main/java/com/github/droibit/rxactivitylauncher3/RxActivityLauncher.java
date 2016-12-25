package com.github.droibit.rxactivitylauncher3;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.SparseArrayCompat;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * Provide a way to receive the results of the {@link Activity} by RxJava.
 * <p>
 * <b>Must call {@link #onActivityResult(int, int, Intent)} method when Activity(Fragment) has received the results.</b><br>
 * </p>
 */
public class RxActivityLauncher {

    private static class TriggeredSubject {

        final boolean hasTrigger;

        final PublishSubject<ActivityResult> actual;

        TriggeredSubject(boolean hasTrigger) {
            this.hasTrigger = hasTrigger;
            this.actual = PublishSubject.create();
        }
    }

    private final Consumer<Object[]> launchActivitySource;

    private final SparseArrayCompat<TriggeredSubject> subjects;

    public RxActivityLauncher(@NonNull Activity activity) {
        this(LaunchActivitySourceFactory.create(activity));
    }

    @VisibleForTesting
    RxActivityLauncher(final Consumer<Object[]> launchActivitySource) {
        this.launchActivitySource = launchActivitySource;
        this.subjects = new SparseArrayCompat<>();
    }
    
    /**
     * Receive a result from the started activity.<br/>
     * Should call in any of the reference methods.
     *
     * @see Activity#onActivityResult(int, int, Intent)
     * @see Fragment#onActivityResult(int, int, Intent)
     * @see android.support.v4.app.Fragment#onActivityResult(int, int, Intent)
     */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    }
}
