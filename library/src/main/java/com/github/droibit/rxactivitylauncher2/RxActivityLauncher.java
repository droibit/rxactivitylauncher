package com.github.droibit.rxactivitylauncher2;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v4.util.SparseArrayCompat;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * Provide a way to receive the results of the {@link Activity} by RxJava.
 * <p>
 * <b>Must call {@link #onActivityResult(int, int, Intent)} method when Activity(Fragment) has received the results.</b><br>
 * </p>
 */
public class RxActivityLauncher {

    private final SparseArrayCompat<Pair<PublishSubject<ActivityResult>, Boolean>> subjects;

    @Nullable
    private final CompositeDisposable compositeDisposable;

    public RxActivityLauncher() {
        this(null);
    }

    public RxActivityLauncher(@Nullable CompositeDisposable compositeDisposable) {
        this.subjects = new SparseArrayCompat<>();
        this.compositeDisposable = compositeDisposable;
    }

    /**
     * Create new {@link LaunchActivitySource} from launch source component({@link Activity}) of other activity.
     *
     * @return New {@link @LaunchActivitySource} instance.
     */
    @NonNull
    @CheckResult
    public LaunchActivitySource from(@NonNull Activity source) {
        return new LaunchActivityFactory.FromActivity(this, source);
    }

    /**
     * Create new {@link LaunchActivitySource} from l launch source component({@link android.support.v4.app.Fragment}) of
     * other
     * activity.
     *
     * @return New {@link @LaunchActivitySource} instance.
     */
    @NonNull
    @CheckResult
    public LaunchActivitySource from(@NonNull android.support.v4.app.Fragment source) {
        return new LaunchActivityFactory.FromSupportFragment(this, source);
    }

    /**
     * Create new {@link LaunchActivitySource} from launch source component({@link Fragment}) of other activity.
     */
    @NonNull
    @CheckResult
    public LaunchActivitySource from(@NonNull Fragment source) {
        return new LaunchActivityFactory.FromFragment(this, source);
    }

    /**
     * Create new {@link LaunchActivitySource} from launch user defined {@link Consumer} of other activity.
     */
    @NonNull
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
        final Pair<PublishSubject<ActivityResult>, Boolean> triggeredSubject = subjects.get(requestCode);
        if (triggeredSubject == null) {
            return;
        }

        final PublishSubject<ActivityResult> subject = triggeredSubject.first;
        final boolean hasTrigger = triggeredSubject.second;
        subject.onNext(new ActivityResult(resultCode, data));

        if (!hasTrigger) {
            subject.onComplete();
            subjects.remove(requestCode);
        }
    }

    Observable<ActivityResult> startActivityForResult(
            final Consumer<Object[]> launchActivityAction,
            final Intent intent, final int requestCode, final Bundle options) {

        final PublishSubject<ActivityResult> subject = createSubjectIfNotExist(requestCode, /*hasTrigger=*/false);
        try {
            launchActivityAction.accept(new Object[]{intent, requestCode, options});
            return subject;
        } catch (ActivityNotFoundException | SecurityException e) {
            subjects.remove(requestCode);
            return Observable.error(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    Observable<ActivityResult> startActivityForResult(
            final Consumer<Object[]> launchActivityAction,
            final Observable<? super Object> trigger,
            final Intent intent, final int requestCode, final Bundle options) {

        final PublishSubject<ActivityResult> subject = createSubjectIfNotExist(requestCode, /*hasTrigger=*/true);
        final Disposable disposable = trigger.subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object ignored) throws Exception {
                try {
                    launchActivityAction.accept(new Object[]{intent, requestCode, options});
                } catch (ActivityNotFoundException | SecurityException e) {
                    subject.onNext(new ActivityResult(e));
                }
            }
        });

        if (compositeDisposable != null) {
            compositeDisposable.add(disposable);
        }
        return subject;
    }

    Observable<ActivityResult> startActivityForResult(final Observable<Action> trigger, int requestCode) {
        final PublishSubject<ActivityResult> subject = createSubjectIfNotExist(requestCode, /*hasTrigger=*/true);
        final Disposable subscription = trigger.subscribe(new Consumer<Action>() {
            @Override
            public void accept(Action action) throws Exception {
                try {
                    action.run();
                } catch (ActivityNotFoundException | SecurityException e) {
                    subject.onNext(new ActivityResult(e));
                }
            }
        });

        if (compositeDisposable != null) {
            compositeDisposable.add(subscription);
        }
        return subject;
    }

    private PublishSubject<ActivityResult> createSubjectIfNotExist(int requestCode, boolean hasTrigger) {
        final Pair<PublishSubject<ActivityResult>, Boolean> targetSubject = subjects.get(requestCode);
        if (targetSubject == null) {
            final PublishSubject<ActivityResult> newSubject = PublishSubject.create();
            subjects.put(requestCode, Pair.create(newSubject, hasTrigger));
            return newSubject;
        }
        return targetSubject.first;
    }
}
