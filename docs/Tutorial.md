# Tutorial

New tutorial structure: 
- Translating logic using Khrysalis
- Translating layouts using Android Layout Translator
- Making an app using RxKotlinPlus
- Translating an app using RxKotlinPlus

This tutorial will teach you how to create a multi-platform project using Khrysalis, RxPlus, and the Android Layout Translator.

## Prerequesites
You'll need the following to begin:
- Android Studio or IntelliJ
- The Android SDK
- Basic Kotlin knowledge
- The will to survive

These will help, but aren't necessary for this tutorial:

- A basic understanding of Gradle
- Android development experience
- iOS development experience
- Typescript web development experience

I'll do my best to provide you with links to additional information as well.


## Create an Android Project

Create an Android project using IntelliJ or Android Studio, with an empty activity.  Make sure you choose to use Kotlin for your build scripts.

Set your language to Kotlin and your minimum supported API to 21.

Make sure you enable view binding like so:

```kotlin
android {
    // ...
    buildFeatures {
        viewBinding = true
    }
    // ...
}
```

## Gradle Changes

Go set up your gradle as shown [in the wiki](https://github.com/lightningkite/khrysalis/wiki/Gradle).

Go set up the Android Layout Translator with the instructions found [here](https://github.com/lightningkite/android-layout-translator/blob/master/README.md).

In addition, add dependencies for RxPlus like so:

```kotlin
plugins {
    // ...
    id("com.lightningkite.khrysalis")
}
val rxPlusVersion: String by extra
dependencies {
    // ...
    implementation("com.lightningkite.rx:view-generator:$rxPlusVersion")
    implementation("com.lightningkite.rx:okhttp:$rxPlusVersion")
    implementation("com.lightningkite.rx:okhttp-resources:$rxPlusVersion")

    equivalents("com.lightningkite.rx:rxplus:$rxPlusVersion:equivalents")
}
```

## Set up your Activity

The default Android project should have created a MainActivity file for you.  Open it up and modify it to look like this:

```kotlin
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lightningkite.khrysalis.android.KhrysalisActivity
import com.lightningkite.khrysalis.views.ViewGenerator

class MainActivity : KhrysalisActivity() {
    companion object {
        val staticMain = RootVG()
    }
    override val main: ViewGenerator
        get() = staticMain
}
```

Don't worry about `RootVG` not being there yet; we're going to make it next.


## Make your first layout

We're going to use standard Android tools to build a "hello world" view.

In IntelliJ/Android Studio, find the `res/layout` folder and rename `main_activity.xml` to `root.xml`, then open the file.

Next, clear out the file and follow this wonderful tutorial I found on standard Android Layout XMLs.

[Tutorial by Chinedu Izuchukwu on code.tutsplus.com](https://code.tutsplus.com/tutorials/beginners-guide-to-android-layout--cms-29984)

You can't use every type of Android view, but you can use a lot of them.  Here's a quick list of what's completely safe:

- AutoCompleteTextView
- Button
- CompoundButton
    - RadioButton
    - Checkbox
    - ToggleButton
- EditText
    - For multiple lines use MultilineEditText instead
- FrameLayout
- ImageView
- LinearLayout
- PageIndicatorView
- ProgressBar
- RatingBar
- RecyclerView
- SeekBar
- Spinner
- SwapView
- TabLayout
- TextView
- VideoPlayer
- View
- ViewFlipper
- ViewPager2


## Make a matching ViewGenerator

A `ViewGenerator` is an object which represents a combination of visual components.  It keeps track of data between loads and coordinates data represented by a view.

We're now going to make the `ViewGenerator` for the layout we made, `root.xml`

ASIDE: There's a nifty tool called the Prototyper that you can use to create the `ViewGenerator`, but we're going to do it by hand in order to understand everything.  The Prototyper will be covered in a different guide.

Next to your `MainActivity.kt` file, we'll add a new folder/package using right-click.  Call it `vg`.

Right click on the new package and create a new Kotlin file.  Name it `RootVG.kt`.

Assuming you've followed the tutorial in the last section, put this inside the new file:

```kotlin
// Replace `something.something` with your application's package.
@file:SharedCode  // Marks this file to be translated
package something.something.vg
import com.lightningkite.khrysalis.SharedCode
import something.something.layouts.RootXml

class RootVG: ViewGenerator() {
    // Defines the title of this view for debugging purposes
    override val title: String = "Root" 

    // Let's store what our TextView should show, because we're going to change it.
    // We're going to us RxJava to keep store values.  A `ValueSubject` is simply a value that we can observe changes on.
    val message: ValueSubject<String> = ValueSubject("Hello!")

    // Creates and configures a view representing this data.
    // A `ViewDependency` is a set of information about an Android Activity and context.
    // In other platforms, this type has other information specific to that platform.
    override fun generate(dependency: ViewDependency): View {
        // `RootBinding` is a class automatically generated by Android.
        val xml = RootBinding()
        // Actually creates the layout in question.  We're now going to set it up and return it.
        val view = xml.setup(dependency)
    
        // bind is an extension function which makes the given TextView always show the Observable in question.
        // It's bidirectional as well.
        message.bind(xml.textView)
    
        // Let's add some code to run when the button is clicked!
        xml.button.setOnClickListener {
            // Yeah, let's just change the message.  It's a simple test to make sure things work like we think.
            this.message.value = "Oh goodness gracious, I've been clicked!"
        }
    
        // Finally finish by returning our configured layout.
        return view
    }
}
```


## Attaching RootVG to our Activity

Jump back to `MainActivity.kt` and make sure our `RootVG` is imported.  If it's not, you can import it by clicking on it and hitting ALT + ENTER.

## Run your Android App

Cool!  Everything should be in order to run the Android app.  Go ahead and try it; check that it works.

## iOS

If you're not on a Mac, you can't build to iOS.  Sorry.

Khrysalis is a converter - as such, we're going to set up a completely normal iOS Cocoapods project within our repo.

Follow [this guide](https://guides.cocoapods.org/using/using-cocoapods.html).

Add the following pods:

```ruby
    # A slightly modified version of RxSwift that enables better use of Subject.
    pod 'RxSwift', :git => 'https://github.com/lightningkite/RxSwift.git', :branch => 'main'
    
    # The iOS equivalent of RxKotlinPlus
    pod 'RxSwiftPlus', :git => 'https://github.com/lightningkite/RxSwiftPlus.git', :branch => 'master'
    
    # A small library required by the layout converter to run properly.
    pod 'XmlToXibRuntime', :git => 'https://github.com/lightningkite/android-layout-translator.git', :branch => 'master'
    
    # A library of code that's required by Khrysalis-translated code.
    pod 'KhrysalisRuntime', :git => 'https://github.com/lightningkite/khrysalis.git', :branch => 'master'
    
    # Adds support for getting location from Rx
    pod "RxCoreLocation", :git => 'https://github.com/RxSwiftCommunity/RxCoreLocation.git', :branch => 'master'
    
    # A version of the Cosmos rating bar that fixes a small bug.  There's an open, unmerged PR for it.
    pod "Cosmos", :git => 'https://github.com/lightningkite/Cosmos.git', :branch => 'master'
```

Now, let's translate what we have over.  Run the Gradle tasks:

- `xmlToXib` - Translates our layouts and resources to iOS
- `compileDebugKotlinToSwift` - Translates our code to Swift

Open the project in XCode and add the newly created files to the project.

Now, we need to use the newly translated pieces.  It's as simple as using the following `AppDelegate`:

```swift
import UIKit
import RxSwiftPlus

@UIApplicationMain
class AppDelegate: ViewGeneratorAppDelegate {
    override func makeMain() -> ViewGenerator {
        UIView.backgroundLayersByName = R.drawable.allEntries  // connects the generated resources to be accessible
        UIView.useLayoutSubviewsLambda()  // allows the translated layouts to use resizing backgrounds correctly
        return RootVG()
    }
}
```

## Web time

Similar to iOS, we can just run `khrysalisWeb` and a suitable Node/Webpack project will be generated.

You can run it using `npm run start`.

## The finish line

Cool, you've got the basics!  You can now go learn how different components work on the examples page of the Khrysalis website.  Here's the order I recommend learning things:

- The `SwapView` example will teach you about going to different views.
- [Rx](http://reactivex.io/) is really helpful to understand and is heavily leveraged by lots of other things.
- Making network requests