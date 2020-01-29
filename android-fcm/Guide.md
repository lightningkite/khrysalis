# Guide

## Set up on Android

Add the following to your manifest inside the `application` tag:

```xml
<meta-data
        android:name="com.google.firebase.messaging.default_notification_icon"
        android:resource="@mipmap/ic_launcher_foreground" />
<meta-data
        android:name="com.google.firebase.messaging.default_notification_channel_id"
        android:value="@string/default_notification_channel_id" />
<service
        android:name="com.lightningkite.khrysalis.fcm.MyFirebaseMessagingService"
        android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```

Add the following to your project-level Gradle file:

```kotlin
apply {
    plugin("com.google.gms.google-services")
}

dependencies {
    implementation("com.lightningkite.khrysalis:android-fcm:0.1.1")
}
```

Add the following to your root Gradle file:

```kotlin
buildscript {
    //...
    dependencies {
        //...
        classpath("com.google.gms:google-services:4.3.3")
    }
}
```

Add the following to your main activity in some kinda init:
```kotlin
import com.lightningkite.khrysalis.fcm.MyFirebaseMessagingService

init {
    MyFirebaseMessagingService.main = viewData
}
```

Drop the `google-services.json` file from the Firebase console into the root directory of your app module.

## Set up on iOS

Add the following to your `podfile` and run `pod install`:

```
pod 'KhrysalisFCM'
```

Use a different app delegate base:

```swift
@UIApplicationMain
class AppDelegate: KhrysalisFcmAppDelegate {
    //...
}
```

Drop the `google-services.plist` file from the Firebase console into the root directory of your app module.

## Usage

Your main view generator can now implement `EntryPoint` to receive the following:

- `fun handleDeepLink(schema: String, host: String, path: String, params: Map<String, String>)`
    - When your notification is tapped and its data contains `deepLink`, the URI will be parsed and hit this function.
- `fun handleNotificationInForeground(map: Map<String, String>): Boolean` 
    - If you receive a notification in the foreground, this function will be hit.  Returning true makes a notification display even while in the app, returning false suppresses the notification.

