package com.github.droibit.rxactivitylauncher;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;

import rx.functions.Action0;
import rx.functions.Action3;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * @author kumagai
 */
public class RxActivityLauncherTest {

    private static final int REQUEST_TEST_1 = 1;

    private static final int REQUEST_TEST_2 = 2;

    private static final int RESULT_OK = -1;

    private static final int RESULT_CANCELED = 0;

    @Mock
    Intent launchIntent;

    @Mock
    Action3<Intent, Integer, Bundle> launchAction;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void startActivityForResult_noTrigger() {
        final RxActivityLauncher launcher = new RxActivityLauncher();

        final TestSubscriber<ActivityResult> testSubscriber = TestSubscriber.create();
        launcher.startActivityForResult(launchAction, launchIntent, REQUEST_TEST_1, null)
                .subscribe(testSubscriber);

        launcher.onActivityResult(REQUEST_TEST_1, RESULT_OK, null);

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertUnsubscribed();
        testSubscriber.assertReceivedOnNext(singletonList(new ActivityResult(RESULT_OK, null)));
    }

    @Test
    public void startActivityForResult_hasTrigger() {
        final RxActivityLauncher launcher = new RxActivityLauncher();

        final PublishSubject<Object> trigger = PublishSubject.create();
        final TestSubscriber<ActivityResult> testSubscriber = TestSubscriber.create();
        launcher.startActivityForResult(launchAction, trigger, launchIntent, REQUEST_TEST_1, null)
                .subscribe(testSubscriber);

        trigger.onNext(null);
        launcher.onActivityResult(REQUEST_TEST_1, RESULT_CANCELED, null);

        testSubscriber.assertNoTerminalEvent();
        testSubscriber.assertReceivedOnNext(singletonList(new ActivityResult(RESULT_CANCELED, null)));

        trigger.onNext(null);
        launcher.onActivityResult(REQUEST_TEST_1, RESULT_OK, null);

        testSubscriber.assertNoTerminalEvent();
        testSubscriber.assertReceivedOnNext(asList(
                new ActivityResult(RESULT_CANCELED, null),
                new ActivityResult(RESULT_OK, null)
        ));
    }

    @Test
    public void startActivityForResult_multipleSubscriptions() {
        final RxActivityLauncher launcher = new RxActivityLauncher();

        final PublishSubject<Object> trigger = PublishSubject.create();
        final TestSubscriber<ActivityResult> s1 = TestSubscriber.create();
        final TestSubscriber<ActivityResult> s2 = TestSubscriber.create();

        launcher.startActivityForResult(launchAction, trigger, launchIntent, REQUEST_TEST_1, null).subscribe(s1);
        launcher.startActivityForResult(launchAction, trigger, launchIntent, REQUEST_TEST_2, null).subscribe(s2);

        trigger.onNext(null);

        launcher.onActivityResult(REQUEST_TEST_1, RESULT_OK, null);
        s1.assertNoTerminalEvent();
        s1.assertReceivedOnNext(singletonList(new ActivityResult(RESULT_OK, null)));

        launcher.onActivityResult(REQUEST_TEST_2, RESULT_CANCELED, null);
        s2.assertNoTerminalEvent();
        s2.assertReceivedOnNext(singletonList(new ActivityResult(RESULT_CANCELED, null)));
    }

    @Test
    public void startActivityForResult_activityRotated() {
        final PublishSubject<Object> trigger1 = PublishSubject.create();
        final RxActivityLauncher launcher1 = new RxActivityLauncher();
        final TestSubscriber<ActivityResult> s1 = TestSubscriber.create();

        launcher1.startActivityForResult(launchAction, trigger1, launchIntent, REQUEST_TEST_1, null)
                .subscribe(s1);

        // After launching the activity, screen is rotated.
        trigger1.onNext(null);
        s1.assertNoValues();

        // A new RxActivityLauncher is created.
        final PublishSubject<Object> trigger2 = PublishSubject.create();
        final RxActivityLauncher launcher2 = new RxActivityLauncher();
        final TestSubscriber<ActivityResult> s2 = TestSubscriber.create();
        launcher2.startActivityForResult(launchAction, trigger2, launchIntent, REQUEST_TEST_1, null)
                .subscribe(s2);

        // A new RxActivityLauncher receives result of the start-up.
        launcher2.onActivityResult(REQUEST_TEST_1, RESULT_OK, null);

        s2.assertNoTerminalEvent();
        s2.assertReceivedOnNext(singletonList(new ActivityResult(RESULT_OK, null)));
    }

    @Test
    public void startActivityForResult_noTrigger_occurError() {
        doThrow(ActivityNotFoundException.class).when(launchAction)
                .call(any(Intent.class), anyInt(), any(Bundle.class));

        final RxActivityLauncher launcher = new RxActivityLauncher();
        final TestSubscriber<ActivityResult> testSubscriber = TestSubscriber.create();
        launcher.startActivityForResult(launchAction, launchIntent, REQUEST_TEST_1, null)
                .subscribe(testSubscriber);

        testSubscriber.assertError(ActivityNotFoundException.class);
        testSubscriber.assertNotCompleted();
    }

    @Test
    public void startActivityForResult_hasTrigger_occurError() {
        final SecurityException exception = new SecurityException();
        doThrow(exception).when(launchAction).call(any(Intent.class), anyInt(), any(Bundle.class));

        final RxActivityLauncher launcher = new RxActivityLauncher();
        final PublishSubject<Object> trigger = PublishSubject.create();
        final TestSubscriber<ActivityResult> testSubscriber = TestSubscriber.create();
        // Error occur.
        launcher.startActivityForResult(launchAction, trigger, launchIntent, REQUEST_TEST_1, null)
                .subscribe(testSubscriber);

        trigger.onNext(null);

        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(singletonList(new ActivityResult(exception)));

        doNothing().when(launchAction).call(any(Intent.class), anyInt(), any(Bundle.class));

        // Launch success.
        trigger.onNext(null);
        launcher.onActivityResult(REQUEST_TEST_1, RESULT_OK, null);

        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(asList(
                new ActivityResult(exception),
                new ActivityResult(RESULT_OK, null)
        ));
    }

    @Test
    public void startActivityForResult_fromAction() {
        final RxActivityLauncher launcher = new RxActivityLauncher();

        final PendingLaunchAction pendingLaunchAction = new PendingLaunchAction();

        final TestSubscriber<ActivityResult> testSubscriber = TestSubscriber.create();
        launcher.startActivityForResult(pendingLaunchAction.trigger, REQUEST_TEST_1)
                .subscribe(testSubscriber);

        final Action0 mockAction1 = mock(Action0.class);
        pendingLaunchAction.invoke(mockAction1);
        launcher.onActivityResult(REQUEST_TEST_1, RESULT_CANCELED, null);

        testSubscriber.assertNoTerminalEvent();
        testSubscriber.assertReceivedOnNext(singletonList(new ActivityResult(RESULT_CANCELED, null)));

        verify(mockAction1).call();

        final Action0 mockAction2 = mock(Action0.class);
        pendingLaunchAction.invoke(mockAction2);
        launcher.onActivityResult(REQUEST_TEST_1, RESULT_OK, null);

        testSubscriber.assertNoTerminalEvent();
        testSubscriber.assertReceivedOnNext(asList(
                new ActivityResult(RESULT_CANCELED, null),
                new ActivityResult(RESULT_OK, null)
        ));
    }
}