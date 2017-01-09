package com.github.droibit.rxactivitylauncher3;

import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * If the activity of launch source is created from the later, to use this class.
 */
public class PendingLaunchAction {

    public final PublishSubject<Consumer<Integer>> trigger;

    public PendingLaunchAction() {
        this.trigger = PublishSubject.create();
    }

    /**
     * Launch the activity from the specified action.
     */
    public void invoke(@NonNull Consumer<Integer> consumer) {
        trigger.onNext(consumer);
    }

    public Observable<Consumer<Integer>> asObservable() {
        return trigger;
    }
}
