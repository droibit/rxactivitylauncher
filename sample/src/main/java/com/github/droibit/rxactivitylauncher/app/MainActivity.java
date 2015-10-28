package com.github.droibit.rxactivitylauncher.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.github.droibit.rxactivitylauncher.ActivityResult;
import com.github.droibit.rxactivitylauncher.RxLauncher;

import rx.functions.Action1;

import static com.github.droibit.rxactivitylauncher.app.DetailActivity.REQUEST_DETAIL;

/**
 * @author kumagai
 */
public class MainActivity extends AppCompatActivity {

    private RxLauncher mLauncher;

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLauncher = RxLauncher.from(this);
    }

    /** {@inheritDoc} */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mLauncher.onActivityResult(requestCode, resultCode, data);
    }

    public void startDetailActivity(View v) {
        final Intent intent = DetailActivity.launchIntent(this, false);
        launchActivity(intent);
    }

    public void startDaggerActivity(View v) {
        final Intent intent = DaggerActivity.launchIntent(this);
        launchActivity(intent);
    }

    private void launchActivity(Intent intent) {
        mLauncher.startActivityForResult(intent, REQUEST_DETAIL)
                .subscribe(new Action1<ActivityResult>() {
                    @Override public void call(ActivityResult result) {
                        final String msg = result.isOk() ? "OK" : "Canceled";
                        Toast.makeText(MainActivity.this, "Received: " + msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
