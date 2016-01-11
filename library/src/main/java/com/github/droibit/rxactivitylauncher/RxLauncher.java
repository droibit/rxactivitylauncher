package com.github.droibit.rxactivitylauncher;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

import static com.github.droibit.rxactivitylauncher.SharedRequest.DEFAULT_SUBJECT_SIZE;

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

    final Map<Integer, PublishSubject<ActivityResult>> mSubjects;
    
    private final Launchable mDelegate;
    private final Set<Integer> mStoredRequests;

    RxLauncher(Launchable delegate) {
        mDelegate = delegate;
        mSubjects = new HashMap<>(DEFAULT_SUBJECT_SIZE);
        mStoredRequests = SharedRequest.restore(delegate.getName());
    }

    /**
     * TODO
     */
    public void destroy() {
        SharedRequest.store(mDelegate.getName(), mSubjects.keySet());

        for (PublishSubject<ActivityResult> subject : mSubjects.values()) {
            subject.onCompleted();
        }
        mSubjects.clear();
    }

    /**
     * Launch an activity for which you would like a result when it finished.
     *
     * @see Activity#startActivityForResult(Intent, int)
     * @see Fragment#startActivityForResult(Intent, int)
     * @see android.support.v4.app.Fragment#startActivityForResult(Intent, int)
     */
    public Observable<ActivityResult> startActivityForResult(@NonNull Intent intent,
                                                             int requestCode) {
        return startActivityForResult(null, intent, requestCode, null);
    }

    /**
     * Launch an activity for which you would like a result when it finished.
     *
     * @see Activity#startActivityForResult(Intent, int, Bundle)
     * @see Fragment#startActivityForResult(Intent, int, Bundle)
     */
    public Observable<ActivityResult> startActivityForResult(@NonNull Intent intent,
                                                             int requestCode,
                                                             @Nullable Bundle options) {
        return startActivityForResult(null, intent, requestCode, options);
    }

    /**
     * Launch an activity for which you would like a result when it finished.
     * <p>
     * After other activity launched, you use this method if the screen might rotate.
     * </p>
     */
    public Observable<ActivityResult> startActivityForResult(@Nullable Observable<?> trigger,
                                                             @NonNull Intent intent,
                                                             int requestCode) {
        return startActivityForResult(trigger, intent, requestCode, null);

    }

    /**
     * Launch an activity for which you would like a result when it finished.
     * <p>
     * After other activity launched, you use this method if the screen might rotate.
     * </p>
     */
    public Observable<ActivityResult> startActivityForResult(@Nullable Observable<?> trigger,
                                                             @NonNull final Intent intent,
                                                             final int requestCode,
                                                             @Nullable final Bundle options) {
        return triggerObservable(trigger, requestCode)
                .flatMap(new Func1<Object, Observable<ActivityResult>>() {
                    @Override
                    public Observable<ActivityResult> call(Object o) {
                        return startActivityObservable(intent, requestCode, options);
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

        if (!mStoredRequests.isEmpty()) {
            mStoredRequests.clear();
        }
    }

    private Observable<?> triggerObservable(Observable<?> trigger, int requestCode) {
        if (trigger == null) {
            return Observable.just(null);
        }

        if (mStoredRequests.contains(requestCode)) {
            return Observable.merge(trigger, Observable.just(null));
        }
        return trigger;
    }

    private Observable<ActivityResult> startActivityObservable(Intent intent,
                                                               int requestCode,
                                                               Bundle options) {
        PublishSubject<ActivityResult> subject = mSubjects.get(requestCode);
        if (subject == null) {
            subject = PublishSubject.create();
            mSubjects.put(requestCode, subject);
        }

        if (!mStoredRequests.contains(requestCode)) {
            try {
                mDelegate.startActivityForResult(intent, requestCode, options);
            } catch (Exception e) {
                return Observable.error(e);
            }
        }
        return subject;
    }
}
