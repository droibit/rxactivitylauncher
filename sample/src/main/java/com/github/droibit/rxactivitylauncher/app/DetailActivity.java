package com.github.droibit.rxactivitylauncher.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * @author kumagai
 */
public class DetailActivity extends AppCompatActivity {

    public static final int REQUEST_DETAIL = 1;
    public static final String KEY_RESPONSE_MSG = "response_msg";

    private static final String EXTRA_REQUEST_MSG = "request_msg";

    public static Intent launchIntent(Context context, boolean requestAppendMsg) {
       return new Intent(context, DetailActivity.class)
               .putExtra(EXTRA_REQUEST_MSG, requestAppendMsg);
    }

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final boolean requestMsg = getIntent().getBooleanExtra(EXTRA_REQUEST_MSG, false);
        if (requestMsg) {
            final Bundle data = new Bundle(1);
            data.putString(KEY_RESPONSE_MSG, "Launched Detail Activity!!!");
            setResult(RESULT_OK, new Intent().putExtras(data));
        } else {
            setResult(RESULT_OK);
        }

        final TextView textView = ((TextView) findViewById(android.R.id.text1));
        textView.setText("Request Message: " + String.valueOf(requestMsg));
    }

    public void cancel(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
