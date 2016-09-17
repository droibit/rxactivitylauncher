package com.github.droibit.rxactivitylauncher.app;

import com.github.droibit.rxactivitylauncher.ActivityResult;
import com.github.droibit.rxactivitylauncher.PendingLaunchAction;
import com.github.droibit.rxactivitylauncher.RxActivityLauncher;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import rx.functions.Action0;
import rx.functions.Action1;

public class ListActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener {

    public static Intent launchIntent(Context context) {
        return new Intent(context, ListActivity.class);
    }

    public static final int REQUEST_DETAIL = 1;

    private final PendingLaunchAction pendingLaunchAction = new PendingLaunchAction();

    private RxActivityLauncher activityLauncher;

    private ArrayAdapter<CharSequence> adapter;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        activityLauncher = new RxActivityLauncher();
        activityLauncher.from(pendingLaunchAction)
                .startActivityForResult(REQUEST_DETAIL)
                .subscribe(new Action1<ActivityResult>() {
                    @Override
                    public void call(ActivityResult result) {
                        final String msg = result.isOk() ? "OK" : "Canceled";
                        showToast("Received: " + msg);
                        Log.d(BuildConfig.BUILD_TYPE, "Start Activity Result: " + msg);
                    }
                });

        adapter = ArrayAdapter.createFromResource(this, R.array.list_items, android.R.layout.simple_list_item_1);
        final ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        activityLauncher.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String item = adapter.getItem(position).toString();
        pendingLaunchAction.invoke(new Action0() {
            @Override
            public void call() {
                startActivityForResult(DetailActivity.launchIntent(ListActivity.this, false, item), REQUEST_DETAIL);
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
