package com.github.droibit.rxactivitylauncher;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


/**
 * Factory class of {@link Launchable}.
 *
 * @author kumagai
 */
class Launchers {

    /**
     * Class to start another {@link Activity}.
     */
    static class SourceActivity implements Launchable {

        private final Activity mActivity;

        SourceActivity(Activity activity) {
            if (activity == null) {
                throw new IllegalArgumentException("Activity should not be null.");
            }
            mActivity = activity;
        }

        /** {@inheritDoc} */
        @Override
        public void startActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle options) {
            mActivity.startActivityForResult(intent, requestCode, options);
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return mActivity.getClass().getName();
        }
    }

    /**
     * Class to start another {@link Activity} from {@link Fragment}
     */
    static class SourceFragment implements Launchable {

        private final Fragment mFragment;

        SourceFragment(Fragment fragment) {
            if (fragment == null) {
                throw new IllegalArgumentException("Fragment should not be null.");
            }
            mFragment = fragment;
        }

        /** {@inheritDoc} */
        @Override
        public void startActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle options) {
            mFragment.startActivityForResult(intent, requestCode, options);
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return mFragment.getClass().getName();
        }
    }

    /**
     * Class to start another {@link Activity} from {@link android.support.v4.app.Fragment}
     */
    static class SourceSupportFragment implements Launchable {

        private final android.support.v4.app.Fragment mFragment;

        SourceSupportFragment(android.support.v4.app.Fragment fragment) {
            if (fragment == null) {
                throw new IllegalArgumentException("Fragment should not be null.");
            }
            mFragment = fragment;
        }

        /** {@inheritDoc} */
        @Override
        public void startActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle options) {
            mFragment.startActivityForResult(intent, requestCode);
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return mFragment.getClass().getName();
        }
    }
}
