package com.github.droibit.rxactivitylauncher;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Provide a way to receive the results of the {@link Activity} by RxJava.
 * <p>
 *  <b>Must call {@link #activityResult(int, int, Intent)} method in Activity or Fragment.</b><br>
 * </p>
 * <p>
 *  If you use the implicit Intent, {@link ActivityNotFoundException} or {@link SecurityException} might occur.
 *  So it is recommended that you use the {@link Observable#subscribe(Observer)}.
 * </p>
 *
 * @author kumagai
 */
public class RxLauncher {

    public static RxLauncher getInstance() {
        return mInstance;
    }
    private static final RxLauncher mInstance = new RxLauncher();

    private final Map<Integer, PublishSubject<ActivityResult>> mSubjects;

    @VisibleForTesting
    RxLauncher() {
        mSubjects = new HashMap<>(3);
    }

    /**
     * When the source component is destroyed, unsubscribe all of the Observers.
     * But, <b>Observable of trigger manually to unsubscribe.</b>
     */
    public void destroy() {
    }

    public Launchable from(Activity source) {
        return new Launchers.SourceActivity(this, source);
    }

    public Launchable from(Fragment source) {
        return new Launchers.SourceFragment(this, source);
    }

    public Launchable from(android.support.v4.app.Fragment source) {
        return new Launchers.SourceSupportFragment(this, source);
    }

    /**
     * Launch an activity for which you would like a result when it finished.
     *
     * @see Activity#startActivityForResult(Intent, int, Bundle)
     * @see Fragment#startActivityForResult(Intent, int, Bundle)
     */
    Observable<ActivityResult> startActivityForResult(@NonNull Launchable source,
                                                      @NonNull Intent intent,
                                                      int requestCode,
                                                      @Nullable Bundle options) {
        return startActivityForResult(source, null, intent, requestCode, options);
    }


    /**
     * Launch an activity for which you would like a result when it finished.
     * <p>
     * After other activity launched, you use this method if the screen might rotate.
     * </p>
     */
    Observable<ActivityResult> startActivityForResult(@NonNull final Launchable source,
                                                      @Nullable Observable<?> trigger,
                                                      @NonNull final Intent intent,
                                                      final int requestCode,
                                                      @Nullable final Bundle options) {
        return triggerObservable(trigger, requestCode)
                .flatMap(new Func1<Object, Observable<ActivityResult>>() {
                    @Override public Observable<ActivityResult> call(Object o) {
                        return startActivityObservable(source, intent, requestCode, options);
                    }
                });
    }

    /**
     * Receive a result from the started activity.<br/>
     * Should call in any of the reference methods.
     *
     * @see Activity#onActivityResult(int, int, Intent)
     * @see Fragment#onActivityResult(int, int, Intent)
     * @see android.support.v4.app.Fragment#onActivityResult(int, int, Intent)
     */
    public void activityResult(int requestCode, int resultCode, @Nullable Intent data) {
        final PublishSubject<ActivityResult> subject = mSubjects.remove(requestCode);
        // There is no subjects, If an error occurs.
        if (subject == null) {
            return;
        }

        subject.onNext(new ActivityResult(resultCode, data));
        subject.onCompleted();
    }

    private Observable<?> triggerObservable(Observable<?> trigger, int requestCode) {
        if (trigger == null) {
            return Observable.just(null);
        }

        if (mSubjects.containsKey(requestCode)) {
            return Observable.merge(trigger, Observable.just(null));
        }
        return trigger;
    }

    private Observable<ActivityResult> startActivityObservable(Launchable source,
                                                               Intent intent,
                                                               int requestCode,
                                                               Bundle options) {
        PublishSubject<ActivityResult> subject = mSubjects.get(requestCode);
        final boolean existSubject = subject != null;
        if (subject == null) {
            subject = PublishSubject.create();
            mSubjects.put(requestCode, subject);
        }

        if (!existSubject) {
            try {
                source.startActivity(intent, requestCode, options);
            } catch (Exception e) {
                return Observable.error(e);
            }
        }
        return subject;
    }
}
