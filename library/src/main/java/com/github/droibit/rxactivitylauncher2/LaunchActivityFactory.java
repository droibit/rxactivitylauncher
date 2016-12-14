package com.github.droibit.rxactivitylauncher2;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;


/**
 * Factory class of {@link LaunchActivitySource}.
 */
class LaunchActivityFactory {

    private LaunchActivityFactory() {
    }

    /**
     * Class to start another {@link Activity}.
     */
    static class SourceActivity implements LaunchActivitySource, Consumer<Object[]> {

        private final RxActivityLauncher launcher;

        private final Activity activity;

        @Nullable
        private Observable<? super Object> trigger;

        SourceActivity(RxActivityLauncher launcher, Activity activity) {
            this.launcher = launcher;
            this.activity = checkNotNull(activity);
        }

        @Override
        public LaunchActivitySource on(@NonNull Observable<? super Object> trigger) {
            this.trigger = checkNotNull(trigger);
            return this;
        }

        @NonNull
        @Override
        public Observable<ActivityResult> startActivityForResult(@NonNull Intent intent, int requestCode,
                @Nullable Bundle options) {
            if (trigger == null) {
                return launcher.startActivityForResult(this, intent, requestCode, options);
            }
            return launcher.startActivityForResult(this, trigger, intent, requestCode, options);
        }

        @Override
        public void accept(Object[] objects) throws Exception {
            activity.startActivityForResult(((Intent) objects[0]), ((int) objects[1]), ((Bundle) objects[2]));
        }
    }

    /**
     * Class to start another {@link Activity} from {@link Fragment}
     */
    static class SourceFragment implements LaunchActivitySource, Consumer<Object[]> {

        private final RxActivityLauncher launcher;

        private final Fragment fragment;

        @Nullable
        private Observable<? super Object> trigger;

        SourceFragment(RxActivityLauncher launcher, Fragment fragment) {
            this.launcher = launcher;
            this.fragment = checkNotNull(fragment);
        }

        @Override
        public LaunchActivitySource on(@NonNull Observable<? super Object> trigger) {
            this.trigger = checkNotNull(trigger);
            return this;
        }

        @NonNull
        @Override
        public Observable<ActivityResult> startActivityForResult(@NonNull Intent intent, int requestCode,
                @Nullable Bundle options) {
            if (trigger == null) {
                return launcher.startActivityForResult(this, intent, requestCode, options);
            }
            return launcher.startActivityForResult(this, trigger, intent, requestCode, options);
        }

        @Override
        public void accept(@NonNull Object[] objects) throws Exception {
            fragment.startActivityForResult(((Intent) objects[0]), ((int) objects[1]), ((Bundle) objects[2]));
        }
    }

    /**
     * Class to start another {@link Activity} from {@link android.support.v4.app.Fragment}
     */
    static class SourceSupportFragment implements LaunchActivitySource, Consumer<Object[]> {

        private final RxActivityLauncher launcher;

        private final android.support.v4.app.Fragment fragment;

        @Nullable
        private Observable<? super Object> trigger;

        SourceSupportFragment(RxActivityLauncher launcher, android.support.v4.app.Fragment fragment) {
            this.launcher = launcher;
            this.fragment = checkNotNull(fragment);
        }

        @Override
        public LaunchActivitySource on(@NonNull Observable<? super Object> trigger) {
            this.trigger = checkNotNull(trigger);
            return this;
        }

        @NonNull
        @Override
        public Observable<ActivityResult> startActivityForResult(@NonNull Intent intent, int requestCode,
                @Nullable Bundle options) {
            if (trigger == null) {
                return launcher.startActivityForResult(this, intent, requestCode, options);
            }
            return launcher.startActivityForResult(this, trigger, intent, requestCode, options);
        }

        @Override
        public void accept(@NonNull Object[] objects) throws Exception {
            fragment.startActivityForResult(((Intent) objects[0]), ((int) objects[1]), ((Bundle) objects[2]));
        }
    }

    @NonNull
    private static <T> T checkNotNull(T object) {
        if (object == null) {
            throw new NullPointerException();
        }
        return object;
    }

    /**
     * Class to start another {@link Activity} from user defined {@link Consumer}.
     */
    static class SourceAction implements UserLaunchActivitySource {

        private final RxActivityLauncher launcher;

        private final UserLaunchAction action;

        SourceAction(RxActivityLauncher launcher, UserLaunchAction action) {
            this.launcher = launcher;
            this.action = checkNotNull(action);
        }

        @NonNull
        @Override
        public Observable<ActivityResult> startActivityForResult(int requestCode) {
            return launcher.startActivityForResult(action.trigger, requestCode);
        }
    }
}
