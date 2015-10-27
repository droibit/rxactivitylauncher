package com.github.droibit.rxactivitylauncher.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * @author kumagai
 */
public class DetailActivity extends AppCompatActivity {

    public static final int REQUEST_DETAIL = 1;

    public static Intent launchIntent(Context context) {
       return new Intent(context, DetailActivity.class);
    }

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setResult(RESULT_OK);
    }
}
