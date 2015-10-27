package com.github.droibit.rxactivitylauncher.app.dagger;

import com.github.droibit.rxactivitylauncher.app.DaggerActivity;

import dagger.Component;

/**
 * @author kumagai
 */
@PerActivity
@Component(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(DaggerActivity daggerActivity);
}
