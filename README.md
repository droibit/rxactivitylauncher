# RxActivityLauncher

[![Build Status](https://travis-ci.org/droibit/rxactivitylauncher.svg?branch=develop)](https://travis-ci.org/droibit/rxactivitylauncher) [![Software License](https://img.shields.io/badge/license-Apache%202.0-brightgreen.svg)](https://github.com/droibit/rxactivitylauncher/blob/develop/LICENSE)  ![Jitpack.io](https://img.shields.io/github/release/droibit/rxactivitylauncher.svg?label=JitPack)

## Download

Add the following code to build.gradle.

```
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.droibit:rxactivitylauncher:0.1.0'
}
```

## Usage

```java
public class MainActivity extends AppCompatActivity {

    private RxLauncher mLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create an instance from the factory method
        mLauncher = RxLauncher.from(this);
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
      mLauncher.startActivityForResult(intent, REQUEST_ANY)
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
      mLauncher.startActivityForResult(intent, REQUEST_ANY)
               .subscribe(new Observer<ActivityResult>() {
                    @Override public void onCompleted() {}

                    @Override public void onError(Throwable e) {
                        // ActivityNotFoundException or SecurityException might occur in implicit Intent.
                    }

                    @Override public void onNext(ActivityResult result) {
                         // Do in the case of received any result.
                    }
              });
    }    
}
```

If you are using a RxLauncher outside Activity/Fragment, [Dagger2](http://google.github.io/dagger/) is useful.
It is also using Dagger2 in the [sample app](https://github.com/droibit/rxactivitylauncher/tree/develop/sample).

## TODO

* Unsubscribe when the screen is destroyed.

# License

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
