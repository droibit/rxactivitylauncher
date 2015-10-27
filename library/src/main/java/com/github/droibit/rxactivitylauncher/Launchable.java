package com.github.droibit.rxactivitylauncher;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Interface to delegate the launch of the activity.
 *
 * @author kumagai
 */
interface Launchable {

    /**
     * Launch an activity for which you would like a result when it finished.
     **/
    void startActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle options);
}
