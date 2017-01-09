package com.github.droibit.rxactivitylauncher3;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.SparseArrayCompat;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
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
        final TriggeredSubject triggeredSubject = subjects.get(requestCode);
        if (triggeredSubject == null) {
            return;
        }
        final PublishSubject<ActivityResult> subject = triggeredSubject.actual;
        subject.onNext(new ActivityResult(resultCode, data));

        if (!triggeredSubject.hasTrigger) {
            subject.onComplete();
            subjects.remove(requestCode);
        }
    }

    /**
     * Launch an activity for which you would like a result when it finished.
     */
    @NonNull
    public Observable<ActivityResult> start(@NonNull Intent intent, int requestCode, @Nullable Bundle options) {
        try {
            launchActivitySource.accept(new Object[]{intent, requestCode, options});
        } catch (ActivityNotFoundException | SecurityException e) {
            return Observable.error(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return createSubjectIfNotExist(requestCode, /*hasTrigger=*/false);
    }

    /**
     * Launch an activity for which you would like a result when it finished.
     */
    @NonNull
    public <T> ObservableTransformer<T, ActivityResult> thenStart(@NonNull final Intent intent, final int requestCode,
            @Nullable final Bundle options) {
        return new ObservableTransformer<T, ActivityResult>() {
            @Override
            public ObservableSource<ActivityResult> apply(Observable<T> trigger) {
                final PublishSubject<ActivityResult> subject = createSubjectIfNotExist(requestCode, /*hasTrigger=*/true);
                trigger.subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        try {
                            launchActivitySource.accept(new Object[]{intent, requestCode, options});
                        } catch (ActivityNotFoundException | SecurityException e) {
                            subject.onNext(new ActivityResult(e));
                        }
                    }
                });
                return subject;
            }
        };
    }

    /**
     * Launch an activity for which you would like a result when it finished.
     *
     */
    @NonNull
    public ObservableTransformer<Consumer<Integer>, ActivityResult> thenStart(final int requestCode) {
        return new ObservableTransformer<Consumer<Integer>, ActivityResult>() {
            @Override
            public ObservableSource<ActivityResult> apply(Observable<Consumer<Integer>> trigger) {
                final PublishSubject<ActivityResult> subject = createSubjectIfNotExist(requestCode, /*hasTrigger=*/true);
                trigger.subscribe(new Consumer<Consumer<Integer>>() {
                    @Override
                    public void accept(Consumer<Integer> consumer) throws Exception {
                        try {
                            consumer.accept(requestCode);
                        } catch (ActivityNotFoundException | SecurityException e) {
                            subject.onNext(new ActivityResult(e));
                        }
                    }
                });
                return subject;
            }
        };
    }

    private PublishSubject<ActivityResult> createSubjectIfNotExist(int requestCode, boolean hasTrigger) {
        final TriggeredSubject existSubject = subjects.get(requestCode);
        if (existSubject != null) {
            return existSubject.actual;
        }
        final TriggeredSubject newSubject = new TriggeredSubject(hasTrigger);
        subjects.put(requestCode, newSubject);
        return newSubject.actual;
    }
}
