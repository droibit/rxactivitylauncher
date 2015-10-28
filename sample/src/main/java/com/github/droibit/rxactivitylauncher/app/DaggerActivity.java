package com.github.droibit.rxactivitylauncher.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.droibit.rxactivitylauncher.ActivityResult;
import com.github.droibit.rxactivitylauncher.RxLauncher;
import com.github.droibit.rxactivitylauncher.app.controller.DaggerController;
import com.github.droibit.rxactivitylauncher.app.dagger.ActivityComponent;
import com.github.droibit.rxactivitylauncher.app.dagger.ActivityModule;
import com.github.droibit.rxactivitylauncher.app.dagger.DaggerActivityComponent;

import javax.inject.Inject;

import rx.functions.Action1;

import static com.github.droibit.rxactivitylauncher.app.DetailActivity.REQUEST_DETAIL;

/**
 * @author kumagai
 */
public class DaggerActivity extends AppCompatActivity {

    public static Intent launchIntent(Context context) {
        return new Intent(context, DaggerActivity.class);
    }

    @Inject
    RxLauncher mLauncher;

    @Inject
    DaggerController mController;

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dagger);

        component().inject(this);
    }

    /** {@inheritDoc} */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mLauncher.onActivityResult(requestCode, resultCode, data);
    }

    public void startDetailActivity(View v) {
        final Intent intent = DetailActivity.launchIntent(this, false);
        mLauncher.startActivityForResult(intent, REQUEST_DETAIL)
                .subscribe(new Action1<ActivityResult>() {
                    @Override public void call(ActivityResult result) {
                        final String msg = result.isOk() ? "OK" : "Canceled";
                        Toast.makeText(DaggerActivity.this, "Received: " + msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void startDetailActivityByController(View v) {
        mController.startDetailActivity();
    }

    public void cancel(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public ActivityComponent component() {
        return DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .build();
    }
}