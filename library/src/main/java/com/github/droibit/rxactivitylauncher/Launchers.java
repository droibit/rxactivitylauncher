package com.github.droibit.rxactivitylauncher;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;


/**
 * Factory class of {@link Launchable}.
 *
 * @author kumagai
 */
class Launchers {

    /**
     * Class to start another {@link Activity}.
     */
    static class SourceActivity extends Launchable {

        private final Activity mActivity;
        private final RxLauncher mLauncher;

        SourceActivity(RxLauncher launcher, Activity activity) {
            if (activity == null) {
                throw new IllegalArgumentException("Activity should not be null.");
            }
            mActivity = activity;
            mLauncher = launcher;
        }

        /** {@inheritDoc} */
        @Override
        public Observable<ActivityResult> startActivityForResult(@NonNull Intent intent,
                                                                 int requestCode,
                                                                 @Nullable Bundle options) {
            return mLauncher.startActivityForResult(this, intent, requestCode, options);
        }

        /** {@inheritDoc} */
        @Override
        public Observable<ActivityResult> startActivityForResult(@Nullable Observable<?> trigger,
                                                                 @NonNull Intent intent,
                                                                 int requestCode,
                                                                 @Nullable Bundle options) {
            return mLauncher.startActivityForResult(this, trigger, intent, requestCode, options);
        }

        /** {@inheritDoc} */
        @Override
        protected void startActivity(@NonNull Intent intent, int requestCode, @Nullable Bundle options) {
            mActivity.startActivityForResult(intent, requestCode, options);
        }
    }

    /**
     * Class to start another {@link Activity} from {@link Fragment}
     */
    static class SourceFragment extends Launchable {

        private final Fragment mFragment;
        private final RxLauncher mLauncher;

        SourceFragment(RxLauncher launcher, Fragment fragment) {
            if (fragment == null) {
                throw new IllegalArgumentException("Fragment should not be null.");
            }
            mFragment = fragment;
            mLauncher = launcher;
        }

        /** {@inheritDoc} */
        @Override
        public Observable<ActivityResult> startActivityForResult(@NonNull Intent intent,
                                                                 int requestCode,
                                                                 @Nullable Bundle options) {
            return mLauncher.startActivityForResult(this, intent, requestCode, options);
        }

        /** {@inheritDoc} */
        @Override
        public Observable<ActivityResult> startActivityForResult(@Nullable Observable<?> trigger,
                                                                 @NonNull Intent intent,
                                                                 int requestCode,
                                                                 @Nullable Bundle options) {
            return mLauncher.startActivityForResult(this, trigger, intent, requestCode, options);
        }

        /** {@inheritDoc} */
        @Override
        protected void startActivity(@NonNull Intent intent, int requestCode, @Nullable Bundle options) {
            mFragment.startActivityForResult(intent, requestCode, options);
        }
    }

    /**
     * Class to start another {@link Activity} from {@link android.support.v4.app.Fragment}
     */
    static class SourceSupportFragment extends Launchable {

        private final android.support.v4.app.Fragment mFragment;
        private final RxLauncher mLauncher;

        SourceSupportFragment(RxLauncher launcher, android.support.v4.app.Fragment fragment) {
            if (fragment == null) {
                throw new IllegalArgumentException("Fragment should not be null.");
            }
            mFragment = fragment;
            mLauncher = launcher;
        }

        /** {@inheritDoc} */
        @Override
        public Observable<ActivityResult> startActivityForResult(@NonNull Intent intent,
                                                                 int requestCode,
                                                                 @Nullable Bundle options) {
            return mLauncher.startActivityForResult(this, intent, requestCode, options);
        }

        /** {@inheritDoc} */
        @Override
        public Observable<ActivityResult> startActivityForResult(@Nullable Observable<?> trigger,
                                                                 @NonNull Intent intent,
                                                                 int requestCode,
                                                                 @Nullable Bundle options) {
            return mLauncher.startActivityForResult(this, trigger, intent, requestCode, options);
        }

        /** {@inheritDoc} */
        @Override
        protected void startActivity(@NonNull Intent intent, int requestCode, /* ignore */ @Nullable Bundle options) {
            mFragment.startActivityForResult(intent, requestCode);
        }
    }
}
