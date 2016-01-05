package com.github.droibit.rxactivitylauncher;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import java.util.Collections;
import java.util.Map;

import rx.Observable;
import rx.Observer;
import rx.subjects.PublishSubject;

/**
 * Provide a way to receive the results of the {@link Activity} by RxJava.
 *
 * Must call {@link #onActivityResult(int, int, Intent)} method in Activity or Fragment.
 *
 * If you use the implicit Intent, {@link ActivityNotFoundException} or {@link SecurityException} might occur.
 * So it is recommended that you use the {@link Observable#subscribe(Observer)}.
 *
 * @author kumagai
 */
public class RxLauncher {

    /**
     * Make {@link RxLauncher} to launch from the {@link Activity}.<br/>
     *
     * @param activity Source {@link Activity}
     */
    public static RxLauncher from(@NonNull Activity activity) {
        return new RxLauncher(new Launchers.SourceActivity(activity));
    }

    /**
     * Make {@link RxLauncher} to launch from the {@link Fragment}.
     *
     * @param fragment Source {@link Fragment}
     */
    public static RxLauncher from(@NonNull Fragment fragment) {
        return new RxLauncher(new Launchers.SourceFragment(fragment));
    }

    /**
     * Make {@link RxLauncher} to launch from the {@link android.support.v4.app.Fragment}.
     *
     * @param fragment Source {@link android.support.v4.app.Fragment}
     */
    public static RxLauncher from(@NonNull android.support.v4.app.Fragment fragment) {
        return new RxLauncher(new Launchers.SourceSupportFragment(fragment));
    }

    private final Launchable mDelegate;

    @VisibleForTesting
    Map<Integer, PublishSubject<ActivityResult>> mSubjects;

    RxLauncher(Launchable delegate) {
        mDelegate = delegate;
        mSubjects = null;
    }

    /**
     * Launch an activity for which you would like a result when it finished.
     *
     * @see Activity#startActivityForResult(Intent, int)
     * @see Fragment#startActivityForResult(Intent, int)
     * @see android.support.v4.app.Fragment#startActivityForResult(Intent, int)
     */
    public Observable<ActivityResult> startActivityForResult(@NonNull Intent intent, int requestCode) {
        return startActivityForResult(intent, requestCode, null);
    }

    /**
     * Launch an activity for which you would like a result when it finished.
     *
     * @see Activity#startActivityForResult(Intent, int, Bundle)
     * @see Fragment#startActivityForResult(Intent, int, Bundle)
     */
    public Observable<ActivityResult> startActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle options) {
        try {
            mDelegate.startActivityForResult(intent, requestCode, options);
        } catch (ActivityNotFoundException | SecurityException e) {
            return Observable.error(e);
        }
        return makeSubject(requestCode);
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
        // There is no subjects, If an error occurs.
        if (mSubjects == null) {
            return;
        }

        final PublishSubject<ActivityResult> subject = mSubjects.get(requestCode);
        subject.onNext(new ActivityResult(resultCode, data));
        subject.onCompleted();

        mSubjects = null;
    }

    private Observable<ActivityResult> makeSubject(int requestCode) {
        if (mSubjects != null) {
            throw new IllegalStateException("Not finished last #startActivityForResult.");
        }

        final PublishSubject<ActivityResult> subject = PublishSubject.create();
        mSubjects = Collections.singletonMap(requestCode, subject);

        return subject;
    }
}
