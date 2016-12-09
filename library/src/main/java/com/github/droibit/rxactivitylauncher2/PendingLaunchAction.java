package com.github.droibit.rxactivitylauncher2;

import android.support.annotation.NonNull;

import io.reactivex.functions.Action;
import io.reactivex.subjects.PublishSubject;

/**
 * If the activity of launch source is created from the later, to use this class.
 */
public class PendingLaunchAction {

    final PublishSubject<Action> trigger = PublishSubject.create();

    public PendingLaunchAction() {
    }

    /**
     * Launch the activity from the specified action.
     */
    public void invoke(@NonNull Action action) {
        trigger.onNext(action);
    }
}
