## Change Log

### Version 2.0.0-beta1 *(2017-01-10)*

**This version includes break change.**

* Support RxJava2.
 * Does not support RxJava 1.x from this version.
* Changed the package.
 * `com.github.droibit.rxactivitylauncher2`
* Abolished `RxActivityLauncher#from`.
* Launch of Activity with Trigger uses `Observable#compose`.

### Version 0.6.0 *(2016-10-26)*

* Update targetSdkVersion to '24'

### Version 0.5.0 *(2016-09-17)*

**This version includes break change.**

* Changed from `RxActivityLauncher#from(Action1<Integer>)` to `RxActivityLauncher#from(PendingLaunchAction)`.

### Version 0.4.1 *(2016-08-22)*

 * Added new method `RxActivityLauncher#from(Action1<Integer>)`.  
   You can specify the launch user-defined action of activity.

### Version 0.4.0 *(2016-08-20)*

**This version includes break change.**

 * Changed the class name to the RxActivityLauncher from RxLauncher.
 * It abolished the Singleton of RxActivityLauncher.  
   Usually, you will create an instance for each Activity(Fragment). If you want to use as a singleton, you should manage your own.
 * Error handling when you are trigger use.

### Version 0.3.0 *(2016-01-21)*

**This version includes break change.**

 * Support the rotation of screen.  
   When launch other activity, specify source component.
