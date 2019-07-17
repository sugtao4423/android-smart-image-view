Smart Image View for Android
==============================

[![](https://jitpack.io/v/sugtao4423/android-smart-image-view.svg)](https://jitpack.io/#sugtao4423/android-smart-image-view)

SmartImageView is a drop-in replacement for Android’s standard ImageView which additionally allows images to be loaded from URLs or the user’s contact address book. Images are cached to memory and to disk for super fast loading.


Features
--------
- Drop-in replacement for ImageView
- Load images from a URL
- Load images from the phone’s contact address book
- Asynchronous loading of images, loading happens outside the UI thread
- Images are cached to memory and to disk for super fast loading
- SmartImage class is easily extendable to load from other sources


Usage
-----
* build.gradle

```
repositories {
    maven {
        url 'https://jitpack.io'
    }
}

dependencies {
    implementation 'com.github.sugtao4423:android-smart-image-view:1.1.0'
}
```


Documentation, Features and Examples
------------------------------------
Full details and documentation can be found on the project page here:

<http://loopj.com/android-smart-image-view/>
