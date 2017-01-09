package com.github.droibit.rxactivitylauncher2.app;

import com.github.droibit.rxactivitylauncher2.ActivityResult;
import com.github.droibit.rxactivitylauncher2.RxActivityLauncher;
import com.jakewharton.rxbinding.view.RxView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.functions.Consumer;
import rx.functions.Func1;

import static com.github.droibit.rxactivitylauncher2.app.DetailActivity.REQUEST_DETAIL;

/**
 * @author kumagai
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ERROR = 2;

    private RxActivityLauncher activityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityLauncher = new RxActivityLauncher(this);

        startDetailActivity(findViewById(R.id.btn_detail));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        activityLauncher.onActivityResult(requestCode, resultCode, data);
    }

    public void startDetailActivity(View v) {
        final Intent intent = DetailActivity.launchIntent(this, false);
        RxJavaInterop.toV2Observable(
                RxView.clicks(v).map(new Func1<Void, Object>() {
                    @Override
                    public Object call(Void aVoid) {
                        return new Object();
                    }
                }))
                .compose(activityLauncher.thenStart(intent, REQUEST_DETAIL, null))
                .subscribe(new Consumer<ActivityResult>() {
                    @Override
                    public void accept(ActivityResult result) throws Exception {
                        final String msg = result.isOk() ? "OK" : "Canceled";
                        showToast("Received: " + msg);
                        Log.d(BuildConfig.BUILD_TYPE, "Start Activity Result: " + msg);
                    }
                });
    }

    public void startListActivity(View v) {
        startActivity(ListActivity.launchIntent(this));
    }

    public void occurActivityNotFondException(View v) {
        final Intent intent = new Intent("test_action");
        occurException(intent);
    }

    public void occurSecurityException(View v) {
        final Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:01234567890"));
        occurException(intent);
    }

    private void occurException(Intent intent) {
        activityLauncher.start(intent, REQUEST_ERROR, null)
                .subscribe(
                        new Consumer<ActivityResult>() {
                            @Override
                            public void accept(ActivityResult result) throws Exception {
                                final String msg = result.isOk() ? "OK" : "Canceled";
                                showToast("Received: " + msg);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                showToast("Error occur: " + throwable.getClass().getSimpleName());
                            }
                        });
    }

    private void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
