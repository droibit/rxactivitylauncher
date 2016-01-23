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

Add the following code to build.gradle.

```
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.droibit:rxactivitylauncher:0.3.0'
}
```

### Usage

```java
public class MainActivity extends AppCompatActivity {

    // Get a singleton instance.
    private RxLauncher mLauncher = RxLauncher.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // If the screen is rotated. Use RxBinding to trigger.
        // https://github.com/JakeWharton/RxBinding
        Observable<Void> trigger = RxView.clicks(findViewById(R.id.button))
        mLauncher.from(this)
                 .startActivityForResult(trigger, intent, REQUEST_ANY, null)
                 .subscribe(result -> {
                     // Do something.
                 }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Always you must call the following #onActivityResult.
        mLauncher.onActivityResult(requestCode, resultCode, data);
    }

    // In the case of explicit intent
    private void startActivityUsingExplicitIntent() {
      Intent intent = new Intent(this, AnyActivity.class);
      mLauncher.from(this)
               .startActivityForResult(intent, REQUEST_ANY, null)
               .subscribe(result -> {
                   if (result.isOk()) {
                       // Do in the case of RESULT_OK  
                   } else {
                       // Do in the case of RESULT_CANCELD
                   }
               });
    }

    // In the case of implicit intent
    private void startActivityUsingImplicitIntent() {
      Intent intent = new Intent(ANY_ACTION);
      mLauncher.from(this)
               .startActivityForResult(intent, REQUEST_ANY, null)
               .subscribe(new Observer<ActivityResult>() {
                    @Override public void onCompleted() {}

                    @Override public void onError(Throwable e) {
                        // Exception might occur in implicit Intent.
                    }

                    @Override public void onNext(ActivityResult result) {
                         // Do in the case of received any result.
                    }
              });
    }
}
```

### Change Log

#### Version 0.3.0 *(2016-01-21)*
 
 * Support the rotation of screen.  
   This version includes a break change. When launch other activity, specify source component. 

## License

    Copyright 2015 droibit

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
