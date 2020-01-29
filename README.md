# Khrysalis

![image](logo.png)

By [Lightning Kite](https://lightningkite.com)

Khrysalis is a low-commitment multiplatform application development system based on converting Android apps into iOS apps.

Khrysalis is actively being used by Lightning Kite to develop apps that are soon to reach production, and is still regularly receiving major upgrades.

## Benefits

- Results in two entirely separate codebases - one in Kotlin for Android, and one in Swift for iOS.  If you want to back out of using Khrysalis, you can do so at any time, just using some basic library pieces from it instead.
- Gradual introduction into codebase, as existing code can be used with new translated code.
- Rapid prototyping, allowing a full click-through app to be created with just XML.
- Full underlying platform access - since you use the underlying languages in their native environments, using native libraries is extremely simple.
- Easy to learn for existing mobile devs, as the core language is Kotlin and you mostly just use normal Kotlin tools.
- Leverages the long-used RX set of libraries, [RXJava](https://github.com/ReactiveX/RxJava) and [RXSwift](https://github.com/ReactiveX/RxSwift), for observable-based programming

## What actually is it?

Khrysalis is the combination of several different parts:

- An Android XML layout to iOS programmatic Swift layout converter
- A Kotlin to Swift syntax transformer
- Several Android-to-iOS resource converters
- A Swift library giving Android-like functionality
- A rapid-prototyping tool, whose output is then modifiable to become a full app.

## Do I have to use all of the features?

The only requirement is that the library portion be imported - other than that you can use only the parts you want.  For example, you could:
- just use the library and syntax transformer to share logic between the two apps
- just use the library and layout converter to share layouts between iOS and Android

## What code can I share between Android and iOS?

Because Khrysalis has a syntax transformer, you can share almost all Kotlin code that:
- uses declarations that have the same signature on both sides
- doesn't require Kotlin's `this` lambda syntax  
- doesn't use Kotlin's implicit `it` parameter lambda syntax
- declares class variables with explicit type
- either uses **all** parameter names or **no** parameter names in function calls
- only has function calls with their arguments in order

The syntax transformation's limitations are whittled away regularly in an effort to make programming easier.

Some features that already have same-signature declarations built for them:

- General UI parts
- RX-based UI programming
- Access to user preferences
- Date/time manipulation
- Loading images
- Vector graphics
- Canvas / Custom views shared across platforms
- JSON translation for data classes
- HTTP/S calls with JSON
- Push notifications via FCM
- Displaying maps via Google Maps and Apple Maps
- Geocoding
- Getting a user's location via GPS
- `ByteBuffer` manipulation
- Bluetooth LE (in progress)

## What do you mean by 'syntax transformer'?

The syntax transformer tool literally takes a valid subset of Kotlin and modifies the syntax to make it fully Swift-compatible.

The syntax transformer does not 'understand' your code, it simply knows the difference between how Kotlin and Swift are written and is able to translate it.  As such, it won't complain if you are accessing Java-specific things at all, though the corresponding Swift code might not have an equivalent declaration and thus might not compile.  It is up to *you* to make sure that both sides have the equivalent declarations.

## What if I need to do something you don't have yet?

Cool!  That's easy - all you have to do is ensure there are same-signature declarations on both sides!

### Simple Example

Let's demonstrate the most basic example of exposing platform-specific code for shared use.  We start by making declarations in Android and iOS separately:

```kotlin
// File is greeting.kt
val greeting: String = "Welcome to the Android App!"
```

```swift
// File is greeting.swift
public let greeting: String = "Welcome to the iOS App!"
```

Now that the declarations match, we can access it in shared code:

```kotlin
// File is demo.shared.kt
// Files with '.shared.kt' at the end can be translated from Kotlin to Swift via a Gradle task
fun demo(){
    println("This is the demo for shared code.")
    println(greeting)
}
```

### Firebase Example

For example, let's say I want to use Firebase Realtime Database on both sides to simply set the user's name.  First, we:

- Set up the library on both apps as normal.
- Ensure the declarations you intend to use have the same signature on both sides, and if not, add stuff to make them match for what you need.

Based on reading the Firebase documentation, we'll make:

```kotlin
// File is MyFirebase.kt in the Android project
object MyFirebase {
    fun get(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
    }
}
```

```swift
// File is MyFirebase.swift in the iOS project
public class MyFirebase {
    private init(){}
    static func get() -> DatabaseReference {
        return Database.database().reference()
    }
}
```

Both sides already define the same signature functions for `DatabaseReference`, including `child(key)` and `setValue(String)`, so we don't have to do anything special to use those in shared code.

Now, in shared code file, we could do this:
```kotlin
// File is MyLogic.shared.kt in the Android Project
// Will get syntax-transformed upon running a task in the project.
fun updateUsersName(forUser: String, newName: String){
    MyFirebase.get().child("users").child(forUser).child("username").setValue(newName)
}
```

You'll likely find that *lots* of different libraries that have been written for both Android and iOS share a fairly similar API, and thus require little-to-no tweaking to be used.

## Usage

```groovy
khrysalis {
    inputDirectory = project.file("")
    outputDirectory = project.file("")
}
```
