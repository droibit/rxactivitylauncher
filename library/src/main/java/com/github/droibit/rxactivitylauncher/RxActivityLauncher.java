package com.github.droibit.rxactivitylauncher;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action3;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Provide a way to receive the results of the {@link Activity} by RxJava.
 * <p>
 * <b>Must call {@link #onActivityResult(int, int, Intent)} method when Activity(Fragment) has received the results.</b><br>
 * </p>
 *
 * @author kumagai
 */
public class RxActivityLauncher {

    private final Map<Integer, Pair<PublishSubject<ActivityResult>, Boolean>> subjects;

    @Nullable
    private final CompositeSubscription compositeSubscription;

    public RxActivityLauncher() {
        this(null);
    }

    public RxActivityLauncher(@Nullable CompositeSubscription compositeSubscription) {
        this.subjects = new HashMap<>();
        this.compositeSubscription = compositeSubscription;
    }

    /**
     * Create new {@link LaunchActivitySource} from launch source component({@link Activity}) of other activity.
     *
     * @return New {@link @LaunchActivitySource} instance.
     */
    @CheckResult
    public LaunchActivitySource from(@NonNull Activity source) {
        return new LaunchActivityFactory.FromActivity(this, source);
    }

    /**
     * Create new {@link LaunchActivitySource} from l launch source component({@link android.support.v4.app.Fragment}) of other
     * activity.
     *
     * @return New {@link @LaunchActivitySource} instance.
     */
    @CheckResult
    public LaunchActivitySource from(@NonNull android.support.v4.app.Fragment source) {
        return new LaunchActivityFactory.FromSupportFragment(this, source);
    }

    /**
     * Create new {@link LaunchActivitySource} from launch source component({@link Fragment}) of other activity.
     */
    @CheckResult
    public LaunchActivitySource from(@NonNull Fragment source) {
        return new LaunchActivityFactory.FromFragment(this, source);
    }

    /**
     * Create new {@link LaunchActivitySource} from launch user defined {@link Action1} of other activity.
     */
    @CheckResult
    public PendingLaunchActivitySource from(@NonNull PendingLaunchAction action) {
        return new LaunchActivityFactory.FromAction(this, action);
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
        final Pair<PublishSubject<ActivityResult>, Boolean> triggerableSubject = subjects.get(requestCode);
        if (triggerableSubject == null) {
            return;
        }

        final PublishSubject<ActivityResult> subject = triggerableSubject.first;
        final boolean hasTrigger = triggerableSubject.second;
        subject.onNext(new ActivityResult(resultCode, data));

        if (!hasTrigger) {
            subject.onCompleted();
            subjects.remove(requestCode);
        }
    }

    Observable<ActivityResult> startActivityForResult(
            final Action3<Intent, Integer, Bundle> launchActivityAction,
            final Intent intent, final int requestCode, final Bundle options) {

        final PublishSubject<ActivityResult> subject = createSubjectIfNotExist(requestCode, /*hasTrigger=*/false);
        try {
            launchActivityAction.call(intent, requestCode, options);
            return subject;
        } catch (ActivityNotFoundException | SecurityException e) {
            subjects.remove(requestCode);
            return Observable.error(e);
        }
    }

    Observable<ActivityResult> startActivityForResult(
            final Action3<Intent, Integer, Bundle> launchActivityAction,
            final Observable<?> trigger,
            final Intent intent, final int requestCode, final Bundle options) {

        final PublishSubject<ActivityResult> subject = createSubjectIfNotExist(requestCode, /*hasTrigger=*/true);
        final Subscription subscription = trigger.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                try {
                    launchActivityAction.call(intent, requestCode, options);
                } catch (ActivityNotFoundException | SecurityException e) {
                    subject.onNext(new ActivityResult(e));
                }
            }
        });

        if (compositeSubscription != null) {
            compositeSubscription.add(subscription);
        }
        return subject;
    }

    Observable<ActivityResult> startActivityForResult(final Observable<Action0> trigger, int requestCode) {

        final PublishSubject<ActivityResult> subject = createSubjectIfNotExist(requestCode, /*hasTrigger=*/true);
        final Subscription subscription = trigger.subscribe(new Action1<Action0>() {
            @Override
            public void call(Action0 action) {
                try {
                    action.call();
                } catch (ActivityNotFoundException | SecurityException e) {
                    subject.onNext(new ActivityResult(e));
                }
            }
        });

        if (compositeSubscription != null) {
            compositeSubscription.add(subscription);
        }
        return subject;
    }

    private PublishSubject<ActivityResult> createSubjectIfNotExist(int requestCode, boolean hasTrigger) {
        if (!subjects.containsKey(requestCode)) {
            final PublishSubject<ActivityResult> newSubject = PublishSubject.create();
            subjects.put(requestCode, Pair.create(newSubject, hasTrigger));
            return newSubject;
        }
        return subjects.get(requestCode).first;
    }
}
