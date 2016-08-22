# RxActivityLauncher

[![Build Status](https://travis-ci.org/droibit/rxactivitylauncher.svg?branch=develop)](https://travis-ci.org/droibit/rxactivitylauncher) [![Software License](https://img.shields.io/badge/license-Apache%202.0-brightgreen.svg)](https://github.com/droibit/rxactivitylauncher/blob/develop/LICENSE)  ![Jitpack.io](https://jitpack.io/v/droibit/rxactivitylauncher.svg)

[RxPermissions](https://github.com/tbruyelle/RxPermissions) inspired me to make this library.

When you receive the result start other activity, must use `Activity#onActivityResult(int, int, Bundle)` or `Fragment#onActivityResult(int, int, Bundle)`.
So, it is troublesome to receive result from the other activity. Library solves this problem by using the [RxJava](https://github.com/ReactiveX/RxJava).

Supports the following classes.

* Activity
* Fragment
* SupportFragment

### Download

Add the following code to `build.gradle`.

```
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}

dependencies {
    compile 'com.github.droibit:rxactivitylauncher:0.4.1'
}
```

### Usage

```java
public class MainActivity extends AppCompatActivity {

    private RxActivityLauncher launcher = new RxActivityLauncher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If the screen is rotated. Use RxBinding to trigger.
        // https://github.com/JakeWharton/RxBinding
        Observable<Void> trigger = RxView.clicks(findViewById(R.id.button))
        launcher.from(this)
                .on(trigger)
                .startActivityForResult(trigger, intent, REQUEST_ANY, null)
                .subscribe(result -> {
                    // If you specify a trigger, even if an exception occurs onError it is not called.
                    // So, the error handling in onNext.
                    if (result.throwable != null) {
                        // Error handling.
                        return;
                    }
                    // Do something.
                 }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Always you must call the following #onActivityResult.
        launcher.onActivityResult(requestCode, resultCode, data);
    }

    // In the case of explicit intent
    private void startActivityUsingExplicitIntent() {
      Intent intent = new Intent(this, AnyActivity.class);
      launcher.from(this)
               .startActivityForResult(intent, REQUEST_ANY, null)
               .subscribe(result -> {
                   if (result.isOk()) {
                       // Do in the case of RESULT_OK  
                   } else {
                       // Do in the case of RESULT_CANCELD etc.
                   }
               });
    }

    // In the case of implicit intent
    private void startActivityUsingImplicitIntent() {
      Intent intent = new Intent(ANY_ACTION);
      launcher.from(this)
               .startActivityForResult(intent, REQUEST_ANY, null)
               .subscribe(new Action1<ActivityResult>() {
                   @Override
                   public void call(ActivityResult result) {
                       // Do in the case of received any result.
                   }
               }, new Action1<Throwable>() {
                   @Override
                   public void call(Throwable throwable) {
                       // Exception might occur in implicit Intent.
                   }
               });
    }
}
```

### Change Log

#### Version 0.4.1 *(2016-08-22)*

 * Added new method `RxActivityLauncher#from(Action1<Integer>)`.  
   You can specify the launch user-defined action of activity.

#### Version 0.4.0 *(2016-08-20)*

**This version includes break change.**

 * Changed the class name to the RxActivityLauncher from RxLauncher.
 * It abolished the Singleton of RxActivityLauncher.  
   Usually, you will create an instance for each Activity(Fragment). If you want to use as a singleton, you should manage your own.
 * Error handling when you are trigger use.

#### Version 0.3.0 *(2016-01-21)*

**This version includes break change.**

 * Support the rotation of screen.  
   When launch other activity, specify source component.

## License

    Copyright 2016 Shinya Kumagai

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
