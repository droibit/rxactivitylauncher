package com.github.droibit.rxactivitylauncher.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.droibit.rxactivitylauncher.ActivityResult;
import com.github.droibit.rxactivitylauncher.RxActivityLauncher;
import com.jakewharton.rxbinding.view.RxView;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static com.github.droibit.rxactivitylauncher.app.DaggerActivity.REQUEST_DAGGER;
import static com.github.droibit.rxactivitylauncher.app.DetailActivity.REQUEST_DETAIL;

/**
 * @author kumagai
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ERROR = 2;

    private RxActivityLauncher activityLauncher;

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityLauncher = new RxActivityLauncher();

        startDetailActivity(findViewById(R.id.btn_detail));
        startDaggerActivity(findViewById(R.id.btn_dagger));
    }

    /** {@inheritDoc} */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        activityLauncher.onActivityResult(requestCode, resultCode, data);
    }

    public void startDetailActivity(View v) {
        final Intent intent = DetailActivity.launchIntent(this, false);

        launchActivity(RxView.clicks(v), intent, REQUEST_DETAIL);
    }

    public void startDaggerActivity(View v) {
        final Intent intent = DaggerActivity.launchIntent(this);

        launchActivity(RxView.clicks(v), intent, REQUEST_DAGGER);
    }

    public void occurActivityNotFondException(View v) {
        final Intent intent = new Intent("test_action");
        occurException(intent);
    }

    public void occurSecurityException(View v) {
        final Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:01234567890"));
        occurException(intent);
    }

    private void launchActivity(Observable<Void> trigger, Intent intent, int requestCode) {
        activityLauncher.from(this)
                .on(trigger)
                .startActivityForResult(intent, requestCode, null)
                .subscribe(new Action1<ActivityResult>() {
                    @Override
                    public void call(ActivityResult result) {
                        final String msg = result.isOk() ? "OK" : "Canceled";
                        showToast("Received: " + msg);
                        Log.d(BuildConfig.BUILD_TYPE, "Start Activity Result: " + msg);
                    }
                });
    }

    private void occurException(Intent intent) {
        activityLauncher.from(this)
                .startActivityForResult(intent, REQUEST_ERROR, null)
                .subscribe(new Action1<ActivityResult>() {
                    @Override
                    public void call(ActivityResult result) {
                        final String msg = result.isOk() ? "OK" : "Canceled";
                        showToast("Received: " + msg);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        showToast("Error occur: " + throwable.getClass().getSimpleName());
                    }
                });

    }

    private void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
