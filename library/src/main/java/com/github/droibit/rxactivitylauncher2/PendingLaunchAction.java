package com.github.droibit.rxactivitylauncher2;

import android.support.annotation.NonNull;

import rx.functions.Action0;
import rx.subjects.PublishSubject;

/**
 * If the activity of launch source is created from the later, to use this class.
 */
public class PendingLaunchAction {

    final PublishSubject<Action0> trigger = PublishSubject.create();

    public PendingLaunchAction() {
    }

    /**
     * Launch the activity from the specified action.
     */
    public void invoke(@NonNull Action0 action) {
        trigger.onNext(action);
    }
}
