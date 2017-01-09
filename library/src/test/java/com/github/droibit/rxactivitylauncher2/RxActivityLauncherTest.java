package com.github.droibit.rxactivitylauncher2;

import android.content.ActivityNotFoundException;
import android.content.Intent;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class RxActivityLauncherTest {

    private enum Notification {
        INSTANCE
    }

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

    @Test
    public void startActivityForResult_noTrigger() {
        final RxActivityLauncher launcher = new RxActivityLauncher();

        final TestObserver<ActivityResult> testObserver = launcher
                .startActivityForResult(launchAction, null, launchIntent, REQUEST_TEST_1, null)
                .test();

        launcher.onActivityResult(REQUEST_TEST_1, RESULT_OK, null);

        testObserver.assertComplete()
                .assertNoErrors()
                .assertValue(new ActivityResult(RESULT_OK, null));
    }

    @Test
    public void startActivityForResult_hasTrigger() {
        final RxActivityLauncher launcher = new RxActivityLauncher();
        final PublishSubject<Object> trigger = PublishSubject.create();

        final TestObserver<ActivityResult> testObserver = launcher
                .startActivityForResult(launchAction, trigger, launchIntent, REQUEST_TEST_1, null)
                .test();

        // first
        {
            trigger.onNext(Notification.INSTANCE);
            launcher.onActivityResult(REQUEST_TEST_1, RESULT_CANCELED, null);

            testObserver.assertNotTerminated()
                .assertValue(new ActivityResult(RESULT_CANCELED, null));
        }

        // second
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

    @Test
    public void startActivityForResult_multipleRequests() {
        final RxActivityLauncher launcher = new RxActivityLauncher();

        final PublishSubject<Object> trigger1 = PublishSubject.create();
        final TestObserver<ActivityResult> testObserver1 = launcher
                .startActivityForResult(launchAction, trigger1, launchIntent, REQUEST_TEST_1, null)
                .test();

        final PublishSubject<Object> trigger2 = PublishSubject.create();
        final TestObserver<ActivityResult> testObserver2 = launcher
                .startActivityForResult(launchAction, trigger2, launchIntent, REQUEST_TEST_2, null)
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
    public void startActivityForResult_rotated() {
        final RxActivityLauncher launcher1 = new RxActivityLauncher();

        final PublishSubject<Object> trigger1 = PublishSubject.create();
        final TestObserver<ActivityResult> testObserver1 = launcher1
                .startActivityForResult(launchAction, trigger1, launchIntent, REQUEST_TEST_1, null)
                .test();

        // After launching the activity, screen is rotated.
        trigger1.onNext(Notification.INSTANCE);
        testObserver1.assertNotTerminated()
                .assertNoValues();

        // Create new RxActivityLauncher.
        final RxActivityLauncher launcher2 = new RxActivityLauncher();
        final PublishSubject<Object> trigger2 = PublishSubject.create();
        final TestObserver<ActivityResult> testObserver2 = launcher2
                .startActivityForResult(launchAction, trigger2, launchIntent, REQUEST_TEST_1, null)
                .test();

        // A new RxActivityLauncher receives result of the start-up.
        launcher2.onActivityResult(REQUEST_TEST_1, RESULT_OK, null);

        testObserver1.assertNotTerminated()
                .assertNoValues();
        testObserver2.assertNotTerminated()
                .assertValue(new ActivityResult(RESULT_OK, null));
    }

    @Test
    public void startActivityForResult_noTriggerWithError() throws Exception {
        final RxActivityLauncher launcher = new RxActivityLauncher();

        doThrow(ActivityNotFoundException.class)
                .when(launchAction).accept(((Object[]) any()));

        final TestObserver<ActivityResult> testObserver = launcher
                .startActivityForResult(launchAction, null, launchIntent, REQUEST_TEST_1, null)
                .test();

        testObserver.assertError(ActivityNotFoundException.class)
                .assertNoValues()
                .assertNotComplete();
    }

    @Test
    public void startActivityForResult_hasTriggerWithError() throws Exception {
        final RxActivityLauncher launcher = new RxActivityLauncher();
        final PublishSubject<Object> trigger = PublishSubject.create();

        final SecurityException exception = new SecurityException();
        doThrow(exception).when(launchAction).accept(((Object[]) any()));

        final TestObserver<ActivityResult> testObserver = launcher
                .startActivityForResult(launchAction, trigger, launchIntent, REQUEST_TEST_1, null)
                .test();

        trigger.onNext(Notification.INSTANCE);

        testObserver.assertNoErrors()
                .assertNotTerminated()
                .assertValue(new ActivityResult(exception));
    }

    @Test
    public void startActivityForResult_fromAction() throws Exception {
        final RxActivityLauncher launcher = new RxActivityLauncher();
        final UserLaunchAction userLaunchAction = new UserLaunchAction();
        final TestObserver<ActivityResult> testObserver = launcher
                .startActivityForResult(userLaunchAction.trigger, REQUEST_TEST_1)
                .test();

        final Action mockAction1 = mock(Action.class);
        userLaunchAction.invoke(mockAction1);
        launcher.onActivityResult(REQUEST_TEST_1, RESULT_CANCELED, null);

        testObserver.assertNotTerminated()
                .assertValue(new ActivityResult(RESULT_CANCELED, null));
        verify(mockAction1).run();

        final Action mockAction2 = mock(Action.class);
        userLaunchAction.invoke(mockAction2);
        launcher.onActivityResult(REQUEST_TEST_1, RESULT_OK, null);


        testObserver.assertNotTerminated()
                .assertValues(
                        new ActivityResult(RESULT_CANCELED, null),
                        new ActivityResult(RESULT_OK, null)
                );
        verify(mockAction2).run();
    }
}