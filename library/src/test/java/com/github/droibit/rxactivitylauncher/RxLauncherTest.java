package com.github.droibit.rxactivitylauncher;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * @author kumagai
 */
public class RxLauncherTest {

    private static final int REQUEST_TEST = 1;
    private static final int RESULT_OK = -1;
    private static final int RESULT_CANCELED = 0;
    private static final int RESULT_FIRST_USER = 1;

    @Mock
    Intent mLaunchIntent;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void launchFromActivity_notrigger() {
        final Activity activity = mock(Activity.class);
        doNothing().when(activity).startActivityForResult(any(Intent.class), anyInt(), any(Bundle.class));

        final RxLauncher launcher = RxLauncher.from(activity);
        final TestSubscriber<ActivityResult> testSubscriber = TestSubscriber.create();
        launcher.startActivityForResult(mLaunchIntent, REQUEST_TEST)
                .subscribe(testSubscriber);

        launcher.activityResult(REQUEST_TEST, RESULT_OK, null);

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertUnsubscribed();
        testSubscriber.assertReceivedOnNext(singletonList(new ActivityResult(RESULT_OK, null)));
    }

    @Test
    public void launchFromActivity_triggerd() {
        final Activity activity = mock(Activity.class);
        doNothing().when(activity).startActivityForResult(any(Intent.class), anyInt(), any(Bundle.class));

        final RxLauncher launcher = RxLauncher.from(activity);

        final PublishSubject<Object> trigger = PublishSubject.create();
        final TestSubscriber<ActivityResult> testSubscriber = TestSubscriber.create();
        launcher.startActivityForResult(trigger, mLaunchIntent, REQUEST_TEST)
                .subscribe(testSubscriber);

        trigger.onNext(null);
        launcher.activityResult(REQUEST_TEST, RESULT_OK, null);

        testSubscriber.assertNoErrors();
        testSubscriber.assertNoTerminalEvent();
        testSubscriber.assertReceivedOnNext(singletonList(new ActivityResult(RESULT_OK, null)));
    }

    @Test
    public void launchFromFragment_notrigger() {
        final Fragment fragment = mock(Fragment.class);
        doNothing().when(fragment).startActivityForResult(any(Intent.class), anyInt(), any(Bundle.class));

        final Activity activity = mock(Activity.class);
        doReturn(activity).when(fragment).getActivity();

        final RxLauncher launcher = RxLauncher.from(fragment);
        final TestSubscriber<ActivityResult> testSubscriber = TestSubscriber.create();
        launcher.startActivityForResult(mLaunchIntent, REQUEST_TEST)
                .subscribe(testSubscriber);

        launcher.activityResult(REQUEST_TEST, RESULT_CANCELED, null);

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertUnsubscribed();
        testSubscriber.assertReceivedOnNext(singletonList(new ActivityResult(RESULT_CANCELED, null)));
    }

    @Test
    public void launchFromFragment_triggered() {
        final Fragment fragment = mock(Fragment.class);
        doNothing().when(fragment).startActivityForResult(any(Intent.class), anyInt(), any(Bundle.class));

        final RxLauncher launcher = RxLauncher.from(fragment);
        final PublishSubject<Object> trigger = PublishSubject.create();
        final TestSubscriber<ActivityResult> testSubscriber = TestSubscriber.create();
        launcher.startActivityForResult(trigger, mLaunchIntent, REQUEST_TEST)
                .subscribe(testSubscriber);

        trigger.onNext(null);
        launcher.activityResult(REQUEST_TEST, RESULT_CANCELED, null);

        testSubscriber.assertNoErrors();
        testSubscriber.assertNoTerminalEvent();
        testSubscriber.assertReceivedOnNext(singletonList(new ActivityResult(RESULT_CANCELED, null)));
    }

    @Test
    public void launchFromSupportFragment_notrigger() {
        final android.support.v4.app.Fragment fragment = mock(android.support.v4.app.Fragment.class);
        doNothing().when(fragment).startActivityForResult(any(Intent.class), anyInt());

        final RxLauncher launcher = RxLauncher.from(fragment);
        final TestSubscriber<ActivityResult> testSubscriber = TestSubscriber.create();
        launcher.startActivityForResult(mLaunchIntent, REQUEST_TEST)
                .subscribe(testSubscriber);

        launcher.activityResult(REQUEST_TEST, RESULT_FIRST_USER, null);

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertUnsubscribed();
        testSubscriber.assertReceivedOnNext(Arrays.asList(new ActivityResult(RESULT_FIRST_USER, null)));
    }

    @Test
    public void launchFromSupportFragment_triggerd() {
        final android.support.v4.app.Fragment fragment = mock(android.support.v4.app.Fragment.class);
        doNothing().when(fragment).startActivityForResult(any(Intent.class), anyInt());

        final RxLauncher launcher = RxLauncher.from(fragment);
        final PublishSubject<Object> trigger = PublishSubject.create();
        final TestSubscriber<ActivityResult> testSubscriber = TestSubscriber.create();
        launcher.startActivityForResult(trigger, mLaunchIntent, REQUEST_TEST)
                .subscribe(testSubscriber);

        trigger.onNext(null);
        launcher.activityResult(REQUEST_TEST, RESULT_FIRST_USER, null);

        testSubscriber.assertNoErrors();
        testSubscriber.assertNoTerminalEvent();
        testSubscriber.assertReceivedOnNext(singletonList(new ActivityResult(RESULT_FIRST_USER, null)));
    }



    @Test
    public void occurActivityNotFoundException() {
        final Activity activity = mock(Activity.class);
        final Exception ane = mock(ActivityNotFoundException.class);
        doThrow(ane).when(activity).startActivityForResult(any(Intent.class), anyInt(), any(Bundle.class));

        final RxLauncher launcher = RxLauncher.from(activity);

        final TestSubscriber<ActivityResult> testSubscriber = TestSubscriber.create();
        launcher.startActivityForResult(mLaunchIntent, REQUEST_TEST)
                .subscribe(testSubscriber);

        launcher.activityResult(REQUEST_TEST, RESULT_CANCELED, null);

        testSubscriber.assertError(ane);
        testSubscriber.assertNotCompleted();
    }

    @Test
    public void occurSecurityException() {
        final Activity activity = mock(Activity.class);
        doThrow(SecurityException.class).when(activity)
                .startActivityForResult(any(Intent.class), anyInt(), any(Bundle.class));

        final RxLauncher launcher = RxLauncher.from(activity);

        final TestSubscriber<ActivityResult> testSubscriber = TestSubscriber.create();
        launcher.startActivityForResult(mLaunchIntent, REQUEST_TEST)
                .subscribe(testSubscriber);

        launcher.activityResult(REQUEST_TEST, RESULT_CANCELED, null);

        testSubscriber.assertError(SecurityException.class);
        testSubscriber.assertNotCompleted();
    }

    // TODO: not impletemnt
    public void unsibscribe() {
    }
}