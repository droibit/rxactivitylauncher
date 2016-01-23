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
import java.util.Collections;

import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
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

        final RxLauncher launcher = new RxLauncher();
        final TestSubscriber<ActivityResult> testSubscriber = TestSubscriber.create();
        launcher.from(activity)
                .startActivityForResult(mLaunchIntent, REQUEST_TEST, null)
                .subscribe(testSubscriber);

        launcher.activityResult(REQUEST_TEST, RESULT_OK, null);

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertUnsubscribed();
        testSubscriber.assertReceivedOnNext(singletonList(new ActivityResult(RESULT_OK, null)));
    }

    @Test
    public void launchFromActivity_triggered() {
        final Activity activity = mock(Activity.class);
        doNothing().when(activity).startActivityForResult(any(Intent.class), anyInt(), any(Bundle.class));

        final RxLauncher launcher = new RxLauncher();

        final PublishSubject<Object> trigger = PublishSubject.create();
        final TestSubscriber<ActivityResult> testSubscriber = TestSubscriber.create();
        launcher.from(activity)
                .startActivityForResult(trigger, mLaunchIntent, REQUEST_TEST, null)
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

        final RxLauncher launcher = new RxLauncher();
        final TestSubscriber<ActivityResult> testSubscriber = TestSubscriber.create();
        launcher.from(fragment)
                .startActivityForResult(mLaunchIntent, REQUEST_TEST, null)
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

        final RxLauncher launcher = new RxLauncher();
        final PublishSubject<Object> trigger = PublishSubject.create();
        final TestSubscriber<ActivityResult> testSubscriber = TestSubscriber.create();
        launcher.from(fragment)
                .startActivityForResult(trigger, mLaunchIntent, REQUEST_TEST, null)
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

        final RxLauncher launcher = new RxLauncher();
        final TestSubscriber<ActivityResult> testSubscriber = TestSubscriber.create();
        launcher.from(fragment)
                .startActivityForResult(mLaunchIntent, REQUEST_TEST, null)
                .subscribe(testSubscriber);

        launcher.activityResult(REQUEST_TEST, RESULT_FIRST_USER, null);

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertUnsubscribed();
        testSubscriber.assertReceivedOnNext(singletonList(new ActivityResult(RESULT_FIRST_USER, null)));
    }

    @Test
    public void launchFromSupportFragment_triggered() {
        final android.support.v4.app.Fragment fragment = mock(android.support.v4.app.Fragment.class);
        doNothing().when(fragment).startActivityForResult(any(Intent.class), anyInt());

        final RxLauncher launcher = new RxLauncher();
        final PublishSubject<Object> trigger = PublishSubject.create();
        final TestSubscriber<ActivityResult> testSubscriber = TestSubscriber.create();
        launcher.from(fragment)
                .startActivityForResult(trigger, mLaunchIntent, REQUEST_TEST, null)
                .subscribe(testSubscriber);

        trigger.onNext(null);
        launcher.activityResult(REQUEST_TEST, RESULT_FIRST_USER, null);

        testSubscriber.assertNoErrors();
        testSubscriber.assertNoTerminalEvent();
        testSubscriber.assertReceivedOnNext(singletonList(new ActivityResult(RESULT_FIRST_USER, null)));
    }

    @Test
    public void multipleSubscription_triggered() {
        final Activity activity = mock(Activity.class);
        doNothing().when(activity).startActivityForResult(any(Intent.class), anyInt(), any(Bundle.class));

        final RxLauncher launcher = new RxLauncher();

        final PublishSubject<Object> trigger = PublishSubject.create();
        final TestSubscriber<ActivityResult> s1 = TestSubscriber.create();
        final TestSubscriber<ActivityResult> s2= TestSubscriber.create();

        launcher.from(activity).startActivityForResult(trigger, mLaunchIntent, REQUEST_TEST, null).subscribe(s1);
        launcher.from(activity).startActivityForResult(trigger, mLaunchIntent, REQUEST_TEST, null).subscribe(s2);

        trigger.onNext(null);
        launcher.activityResult(REQUEST_TEST, RESULT_OK, null);

        for (TestSubscriber<ActivityResult> subscriber : Arrays.asList(s1, s2)) {
            subscriber.assertNoErrors();
            subscriber.assertNoTerminalEvent();
            subscriber.assertReceivedOnNext(singletonList(new ActivityResult(RESULT_OK, null)));
        }
    }

    @Test
    public void destroyActivityAfterLaunch() {
        final Activity activity = mock(Activity.class);
        doNothing().when(activity).startActivityForResult(any(Intent.class), anyInt(), any(Bundle.class));

        final PublishSubject<Object> trigger = PublishSubject.create();

        final RxLauncher launcher1 = RxLauncher.getInstance();
        final TestSubscriber<ActivityResult> s1 = TestSubscriber.create();
        launcher1.from(activity)
                .startActivityForResult(trigger, mLaunchIntent, REQUEST_TEST, null)
                .subscribe(s1);

        trigger.onNext(null);
        s1.assertNoValues();
        s1.unsubscribe();

        final RxLauncher launcher2 = RxLauncher.getInstance();
        final TestSubscriber<ActivityResult> s2 = TestSubscriber.create();
        launcher2.from(activity)
                 .startActivityForResult(trigger, mLaunchIntent, REQUEST_TEST, null)
                 .subscribe(s2);

        launcher2.activityResult(REQUEST_TEST, RESULT_CANCELED, null);

        s2.assertNoErrors();
        s2.assertNoTerminalEvent();
        s2.assertReceivedOnNext(singletonList(new ActivityResult(RESULT_CANCELED, null)));
    }

    @Test
    public void occurActivityNotFoundException() {
        final Activity activity = mock(Activity.class);
        final Exception ane = mock(ActivityNotFoundException.class);
        doThrow(ane).when(activity).startActivityForResult(any(Intent.class), anyInt(), any(Bundle.class));

        final RxLauncher launcher = new RxLauncher();

        final TestSubscriber<ActivityResult> testSubscriber = TestSubscriber.create();
        launcher.from(activity)
                .startActivityForResult(mLaunchIntent, REQUEST_TEST, null)
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

        final RxLauncher launcher = new RxLauncher();

        final TestSubscriber<ActivityResult> testSubscriber = TestSubscriber.create();
        launcher.from(activity)
                .startActivityForResult(mLaunchIntent, REQUEST_TEST, null)
                .subscribe(testSubscriber);

        launcher.activityResult(REQUEST_TEST, RESULT_CANCELED, null);

        testSubscriber.assertError(SecurityException.class);
        testSubscriber.assertNotCompleted();
    }

    // TODO: not impletemnt
    public void unsibscribe() {
    }
}