package com.github.droibit.rxactivitylauncher.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.droibit.rxactivitylauncher.ActivityResult;
import com.github.droibit.rxactivitylauncher.RxLauncher;
import com.jakewharton.rxbinding.view.RxView;

import rx.Observable;
import rx.Observer;
import rx.functions.Action1;

import static com.github.droibit.rxactivitylauncher.app.DetailActivity.REQUEST_DETAIL;

/**
 * @author kumagai
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ERROR = 2;

    private RxLauncher mLauncher;

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLauncher = RxLauncher.from(this);

        startDetailActivity(findViewById(R.id.btn_detail));
        startDaggerActivity(findViewById(R.id.btn_dagger));
    }

    /** {@inheritDoc} */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mLauncher.activityResult(requestCode, resultCode, data);
    }

    /** {@inheritDoc} */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        mLauncher.destroy();
    }

    public void startDetailActivity(View v) {
        final Intent intent = DetailActivity.launchIntent(this, false);

        launchActivity(RxView.clicks(v), intent);
    }

    public void startDaggerActivity(View v) {
        final Intent intent = DaggerActivity.launchIntent(this);

        launchActivity(RxView.clicks(v), intent);
    }

    public void occurActivityNotFondException(View v) {
        final Intent intent = new Intent("test_action");
        occurException(intent);
    }

    public void occurSecurityException(View v) {
        final Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:01234567890"));
        occurException(intent);
    }

    private void launchActivity(Observable<Void> trigger, Intent intent) {
        mLauncher.startActivityForResult(trigger, intent, REQUEST_DETAIL)
                 .subscribe(new Action1<ActivityResult>() {
                     @Override public void call(ActivityResult result) {
                         final String msg = result.isOk() ? "OK" : "Canceled";
                         showToast("Received: " + msg);
                         Log.d(BuildConfig.BUILD_TYPE, "Start Activity Result: " + msg);
                     }
                 });
    }

    private void occurException(Intent intent) {
        mLauncher.startActivityForResult(intent, REQUEST_ERROR)
                 .subscribe(new Observer<ActivityResult>() {
                     @Override public void onCompleted() {}

                     @Override public void onError(Throwable e) {
                         showToast("Error occur: " + e.getClass().getSimpleName());
                     }

                     @Override public void onNext(ActivityResult result) {
                         final String msg = result.isOk() ? "OK" : "Canceled";
                         showToast("Received: " + msg);
                     }
                 });
    }

    private void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
