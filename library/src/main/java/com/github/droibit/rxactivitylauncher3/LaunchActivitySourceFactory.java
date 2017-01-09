package com.github.droibit.rxactivitylauncher3;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import io.reactivex.functions.Consumer;

import static android.support.annotation.RestrictTo.Scope.LIBRARY;
import static io.reactivex.internal.functions.ObjectHelper.requireNonNull;

@RestrictTo(LIBRARY)
final class LaunchActivitySourceFactory {

    private LaunchActivitySourceFactory() {
    }

    @NonNull
    static Consumer<Object[]> create(final Activity activity) {
        requireNonNull(activity, "activity must be not null.");
        return new Consumer<Object[]>() {
            @Override
            public void accept(Object[] objects) throws Exception {
                ActivityCompat.startActivityForResult(activity, ((Intent) objects[0]), ((int) objects[1]),
                        ((Bundle) objects[2]));
            }
        };
    }

    @NonNull
    static Consumer<Object[]> create(final Fragment fragment) {
        requireNonNull(fragment, "fragment must be not null.");
        return new Consumer<Object[]>() {
            @Override
            public void accept(Object[] objects) throws Exception {
                fragment.startActivityForResult(((Intent) objects[0]), ((int) objects[1]), ((Bundle) objects[2]));
            }
        };
    }
}
