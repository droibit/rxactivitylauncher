package com.github.droibit.rxactivitylauncher2;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import android.content.Intent;

import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;


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
                .startActivityForResult(launchAction, launchIntent, REQUEST_TEST_1, null)
                .test();

        launcher.onActivityResult(REQUEST_TEST_1, RESULT_OK, null);

        testObserver.assertComplete()
                .assertNoErrors()
                .assertValue(new ActivityResult(RESULT_OK, null));
    }

    @Test
    public void startActivityForResult_hasTrigger() {
        final PublishSubject<Object> trigger = PublishSubject.create();
        final RxActivityLauncher launcher = new RxActivityLauncher();
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
    public void startActivityForResult_multipleSubscriptions() {

    }
}