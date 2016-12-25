package com.github.droibit.rxactivitylauncher3;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * This class holds the data received by {@link Activity#onActivityResult(int, int, Intent)}.
 */
public class ActivityResult {

    public final int resultCode;

    @Nullable
    public final Intent data;

    @Nullable
    public final Throwable throwable;

    ActivityResult(int resultCode, @Nullable Intent data) {
        this.resultCode = resultCode;
        this.data = data;
        this.throwable = null;
    }

    ActivityResult(@NonNull Throwable throwable) {
        this.resultCode = Integer.MIN_VALUE;
        this.throwable = throwable;
        this.data = null;
    }

    public boolean isOk() {
        return resultCode == Activity.RESULT_OK;
    }

    public boolean isCanceled() {
        return resultCode == Activity.RESULT_CANCELED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActivityResult)) {
            return false;
        }

        ActivityResult that = (ActivityResult) o;

        if (resultCode != that.resultCode) {
            return false;
        }
        //noinspection SimplifiableIfStatement
        if (data != null ? !data.equals(that.data) : that.data != null) {
            return false;
        }
        return throwable != null ? throwable.equals(that.throwable) : that.throwable == null;
    }

    @Override
    public int hashCode() {
        int result = resultCode;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (throwable != null ? throwable.hashCode() : 0);
        return result;
    }
}
