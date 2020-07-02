# Tutorial

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


## Getting Khrysalis

First, you need to get Khrysalis.

Since Khrysalis is still in early development, you'll have to download the repository and build it yourself. A repository-based release is expected by the end of the year.

Download/clone the GitHub project [here](https://github.com/lightningkite/khrysalis).

Then, run the script called 'firstTime'. It may take a minute, but this script will build and put Khrysalis in your local Maven repository.


## Create an Android Project

Create an Android project using IntelliJ or Android Studio, with an empty activity.

Set your language to Kotlin and your minimum supported API to 21.


## Add the Khrysalis Gradle plugin

Open `app/build.gradle`.

At the top of your file, add:

```groovy
buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath("com.lightningkite.khrysalis:plugin:+")
    }
}
apply(plugin="com.lightningkite.khrysalis")
```

In the dependencies section, add:

```groovy
implementation "com.lightningkite.khrysalis:android:0.1.1"
```

Now, re-sync the project using CTRL + SHIFT + A and typing 'gradle sync' and pressing enter.


## Set up your Activity

The default Android project should have created a MainActivity file for you.  Open it up and modify it to look like this:

```kotlin
//package name...
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
- ViewPager


## Make a matching ViewGenerator

A `ViewGenerator` is an object which represents a combination of visual components.  It keeps track of data between loads and coordinates data represented by a view.

We're now going to make the `ViewGenerator` for the layout we made, `root.xml`

There's a nifty tool called the Prototyper that you can use to create the `ViewGenerator`, but we're going to do it by hand in order to understand everything.  The Prototyper will be covered in a different guide.

Next to your `MainActivity.kt` file, we'll add a new folder/package using right-click.  Call it `vg`.

Right click on the new package and create a new Kotlin file.  Name it `RootVG.shared.kt`.

The `.shared` is important here - it marks the file for translation to the different platforms.

Next, we need to run a Gradle task to help us get some autocomplete stuff.  Run the Gradle task `khrysalisAndroid`.  This task will generate a bunch of Kotlin classes representing the layouts we've made so far, giving them strict typing and complete information.

Assuming you've followed the tutorial in the last section, put this inside the new file:

```kotlin
// Replace `something.something` with your application's package.
package something.something.vg
import something.something.layouts.RootXml

class RootVG: ViewGenerator() {
    // Defines the title of this view for debugging purposes
    override val title: String = "Root" 

    // Let's store what our TextView should show, because we're going to change it.
    // An `ObservableProperty` is exactly that - a property, or piece of information, on which we can observe changes.
    // This uses something called RX under the hood, which you should go learn about.
    // I highly recommend looking at the source for these properties, as the basic ones are both simple (sub 25 lines) and useful.
    // We use observable properties to keep our user interface and information in perfect sync. 
    val message: StandardObservableProperty<String> = StandardObservableProperty("Hello!")

    // Creates and configures a view representing this data.
    // A `ViewDependency` is a set of information about an Android Activity and context.
    // In other platforms, this type has other information specific to that platform.
    override fun generate(dependency: ViewDependency): View {
        // `RootXml` is a class automatically generated by the task mentioned above.
        val xml = RootXml()
        // Actually creates the layout in question.  We're now going to set it up and return it.
        val view = xml.setup(dependency)
    
        // bindString is an extension function which makes the given TextView always show the ObservableProperty in question.
        xml.textView.bindString(message)
    
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

## iOS time

If you're not on a Mac, you can't build to iOS.  Sorry.

If you are on a Mac, though, we can build to iOS pretty easily!

There's a handy shortcut build into the plugin which will set up a standard iOS project for you and put everything you need into it.  The task is called `khrysalisiOS`.  Run it and open the generated workspace in XCode and compile away!

The output XCode project is a normal iOS Swift project that uses Cocoapods.  Those familiar with iOS will find nothing surprising in the generated code, and it will, in fact, be quite readable.

## Web time

Similar to iOS, we can just run `khrysalisWeb` and a suitable Node/Webpack project will be generated.

You can run it using `npm run start`.

You're free to edit the files, just note that any file with `.shared` or `.actual` will be overwritten unless you've removed the flag at the top that indicates the file will be overwritten on translation.

## The finish line

Cool, you've got the basics!  You can now go learn how different components work on the examples page of the Khrysalis website.  Here's the order I recommend learning things:

- The `SwapView` example will teach you about going to different views.
- [Rx](http://reactivex.io/) is really helpful to understand and is heavily leveraged by lots of other things.
- Making network requests