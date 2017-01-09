package com.github.droibit.rxactivitylauncher2;

import com.github.droibit.rxactivitylauncher2.internal.Notification;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import android.content.ActivityNotFoundException;
import android.content.Intent;

import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RxActivityLauncherTest {

    private static final int REQUEST_TEST_1 = 1;

    private static final int REQUEST_TEST_2 = 2;

    private static final int RESULT_OK = -1;

    private static final int RESULT_CANCELED = 0;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    Intent launchIntent;

    @Mock
    Consumer<Object[]> launchAction;

    private RxActivityLauncher launcher;

    @Before
    public void setUp() {
        launcher = new RxActivityLauncher(launchAction);
    }

    @Test
    public void start() {
        final TestObserver<ActivityResult> testObserver = launcher
                .start(launchIntent, REQUEST_TEST_1, null)
                .test();

        launcher.onActivityResult(REQUEST_TEST_1, RESULT_OK, null);

        testObserver
                .assertNoErrors()
                .assertComplete()
                .assertValue(new ActivityResult(RESULT_OK, null));

    }

    @Test
    public void thenStart() {
        final PublishSubject<Object> trigger = PublishSubject.create();
        final TestObserver<ActivityResult> testObserver = trigger
                .compose(launcher.thenStart(launchIntent, REQUEST_TEST_1, null))
                .test();

        // run first
        {
            trigger.onNext(Notification.INSTANCE);
            launcher.onActivityResult(REQUEST_TEST_1, RESULT_CANCELED, null);

            testObserver.assertNotTerminated()
                    .assertValue(new ActivityResult(RESULT_CANCELED, null));
        }

        // run second
        {
            {
                trigger.onNext(Notification.INSTANCE);
                launcher.onActivityResult(REQUEST_TEST_1, RESULT_OK, null);

                testObserver.assertNotTerminated()
                        .assertValues(
                                new ActivityResult(RESULT_CANCELED, null),
                                new ActivityResult(RESULT_OK, null)
                        );

            }
        }
    }

    @Test
    public void thenStart_multipleRequests() {
        final PublishSubject<Object> trigger1 = PublishSubject.create();
        final TestObserver<ActivityResult> testObserver1 = trigger1
                .compose(launcher.thenStart(launchIntent, REQUEST_TEST_1, null))
                .test();

        final PublishSubject<Object> trigger2 = PublishSubject.create();
        final TestObserver<ActivityResult> testObserver2 = trigger2
                .compose(launcher.thenStart(launchIntent, REQUEST_TEST_2, null))
                .test();

        // fire trigger1
        {
            trigger1.onNext(Notification.INSTANCE);
            launcher.onActivityResult(REQUEST_TEST_1, RESULT_OK, null);

            testObserver1.assertNotTerminated()
                    .assertValue(new ActivityResult(RESULT_OK, null));
            testObserver2.assertNotTerminated()
                    .assertNoValues();
        }

        // fire trigger2
        {
            trigger2.onNext(Notification.INSTANCE);
            launcher.onActivityResult(REQUEST_TEST_2, RESULT_CANCELED, null);

            testObserver1.assertNotTerminated()
                    .assertValueCount(1);
            testObserver2.assertNotTerminated()
                    .assertValue(new ActivityResult(RESULT_CANCELED, null));
        }
    }

    @Test
    public void thenStart_rotated() {
        final RxActivityLauncher launcher1 = new RxActivityLauncher(launchAction);
        final PublishSubject<Object> trigger1 = PublishSubject.create();
        final TestObserver<ActivityResult> testObserver1 = trigger1
                .compose(launcher.thenStart(launchIntent, REQUEST_TEST_1, null))
                .test();

        // After launching the activity, screen is rotated.
        trigger1.onNext(Notification.INSTANCE);
        testObserver1
                .assertNotTerminated()
                .assertNoValues();

        // Create new RxActivityLauncher.
        final RxActivityLauncher launcher2 = new RxActivityLauncher(launchAction);
        final PublishSubject<Object> trigger2 = PublishSubject.create();
        final TestObserver<ActivityResult> testObserver2 = trigger2
                .compose(launcher2.thenStart(launchIntent, REQUEST_TEST_1, null))
                .test();

        // A new RxActivityLauncher receives result of the start-up.
        launcher2.onActivityResult(REQUEST_TEST_1, RESULT_OK, null);

        testObserver1
                .assertNotTerminated()
                .assertNoValues();
        testObserver2
                .assertNotTerminated()
                .assertValue(new ActivityResult(RESULT_OK, null));
    }

    @Test
    public void start_occurError() throws Exception {
        // Occur ActivityNotFoundException
        {
            doThrow(ActivityNotFoundException.class)
                    .when(launchAction).accept(((Object[]) any()));

            final TestObserver<ActivityResult> testObserver = launcher.start(launchIntent, REQUEST_TEST_1, null)
                    .test();

            testObserver
                    .assertError(ActivityNotFoundException.class)
                    .assertNotComplete()
                    .assertNoValues();
        }

        // Occur SecurityException
        {
            doThrow(SecurityException.class)
                    .when(launchAction).accept(((Object[]) any()));

            final TestObserver<ActivityResult> testObserver = launcher.start(launchIntent, REQUEST_TEST_1, null)
                    .test();

            testObserver
                    .assertError(SecurityException.class)
                    .assertNotComplete()
                    .assertNoValues();
        }
    }

    @Test
    public void thenStart_occurError() throws Exception {
        // Occur ActivityNotFoundException
        {
            final ActivityNotFoundException exception = new ActivityNotFoundException();
            doThrow(exception)
                    .when(launchAction).accept(((Object[]) any()));

            final PublishSubject<Object> trigger = PublishSubject.create();
            final TestObserver<ActivityResult> testObserver = trigger
                    .compose(launcher.thenStart(launchIntent, REQUEST_TEST_1, null))
                    .test();

            trigger.onNext(Notification.INSTANCE);

            testObserver
                    .assertNotTerminated()
                    .assertValue(new ActivityResult(exception));
        }

        // Occur SecurityException
        {
            final SecurityException exception = new SecurityException();
            doThrow(exception)
                    .when(launchAction).accept(((Object[]) any()));

            final PublishSubject<Object> trigger = PublishSubject.create();
            final TestObserver<ActivityResult> testObserver = trigger
                    .compose(launcher.thenStart(launchIntent, REQUEST_TEST_1, null))
                    .test();

            trigger.onNext(Notification.INSTANCE);

            testObserver
                    .assertNotTerminated()
                    .assertValue(new ActivityResult(exception));
        }
    }

    @Test
    public void thenStart_usingPendingLaunchAction() {
        final PendingLaunchAction pendingLaunchAction = new PendingLaunchAction();
        final TestObserver<ActivityResult> testObserver = pendingLaunchAction.asObservable()
                .compose(launcher.thenStart(REQUEST_TEST_1))
                .test();

        //noinspection unchecked
        final Consumer<Integer> mockConsumer = (Consumer<Integer>) mock(Consumer.class);
        pendingLaunchAction.invoke(mockConsumer);

        launcher.onActivityResult(REQUEST_TEST_1, RESULT_CANCELED, null);

        testObserver
                .assertNotTerminated()
                .assertValue(new ActivityResult(RESULT_CANCELED, null));

    }
}
