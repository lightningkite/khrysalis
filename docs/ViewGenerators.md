# View Generators

A `ViewGenerator` is a screen or part of a screen in your app.  These are based on [ReactiveX](http://reactivex.io/) and [Android XMLs](https://developer.android.com/guide/topics/ui/declaring-layout).

### Why not standard Android activities or fragments?

Simply put, they're *really* irritating to work with, even from just a plain Android perspective.  They don't retain information between screen rotations, can't nest very effectively, and disallow constructors.

Thus, we wanted to make an alternative that worked well across platforms and allowed us to escape the limitations.  We instead just use a single `Activity` and put `ViewGenerator`s inside of it, pushing and popping them as needed.

### What is a `ViewGenerator`?

As per its name, it is simply a basic class with a function to generate a functional Android layout.  It looks like this in the Khrysalis source:

```kotlin
// Simplified
abstract class ViewGenerator {
    abstract fun generate(dependency: ViewDependency): View
}
```

### What is a `ViewDependency`?

A `ViewDependency` is a platform-specific class that contains information needed to generate a view.

In Android, it has a context and activity access information.

In iOS, it has a reference to the main `UIViewController`.

In Web, it is simply an alias for the `window`.


### Cool, how do I make one?

Most view generators consist of two parts: the XML layout and the code.

Layouts are done using plain Android XML.  You can read more details [here](Layouts.md).

We're going to make a simple screen with a counter on it.  Make a new Android layout called `example.xml` in `src/main/res/layout` and put this in it:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="8dp">

    <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_margin="8dp"
            android:textSize="20sp"
            android:text="My First View"
            />

    <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_margin="8dp"
            android:text="Welcome to my first view!"
            />

    <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center">

        <TextView
                android:id="@+id/number"
                android:layout_width="0px"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_margin="8dp"
                android:text="0"
        />

        <Button
                android:id="@+id/increment"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_margin="8dp"
                android:text="Increment"
        />
    </LinearLayout>
</LinearLayout>
```

Cool, we've got the layout setup.  Now, we run the `khrysalisAndroid` [task in Gradle](https://www.jetbrains.com/help/idea/work-with-gradle-tasks.html).  This task will create a class for each layout file that provides type-safe access to the views within your layout.

Let's make the corresponding `ViewGenerator` now:

```kotlin
package mypackage.name.here

import android.view.View
import com.lightningkite.khrysalis.observables.binding.*
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.views.*
import mypackage.name.here.layouts.ExampleContentXml

class ExampleVG : ViewGenerator() {
    val number = StandardObservableProperty(0)

    fun increment() {
        number.value += 1
    }

    override fun generate(dependency: ViewDependency): View {
        val xml = ExampleXml()
        val view = xml.setup(dependency)
        xml.increment.onClick { this.increment() }
        xml.number.bindString(number.map { it.toString() })
        return view
    }
}
```

All right, let's take this a small section at a time.

### Declare the view generator

Here we define our new view generator.

```kotlin
class ExampleVG : ViewGenerator() {
    //...
}
```

### Store some data

For our view with a counter, we need to keep track of the number.  We do this using an `ObservableProperty`.

```kotlin
val number = StandardObservableProperty(0)
```

An `ObservableProperty` is exactly what it sounds like - a property that you can observe for changes.  It's an extension of [RxJava](http://reactivex.io/) that is specifically designed for making UI using RX, and like the rest of RX, there are a ton of operators you can use on them.  The interface for an `ObservableProperty` is really simple:

```kotlin
abstract class ObservableProperty<T> {
    abstract val value: T
    abstract val onChange: Observable<Box<T>>
}
```

In other words, it is simply an accessible value with a `onChange` event using Rx's `Observable`.  There's a `Box` that ensures that it is safe to use `null`.

There's a subclass of this called `MutableObservableProperty<T>` that lets you modify the value inside.

A `StandardObservableProperty` is one that works as simply as possible, storing a value and emitting an event whenever it is changed.  You'll use them a lot, and we're using one here for our counter.

### Add an action to increment the counter

```kotlin
fun increment() {
    number.value += 1
}
```

### Inflate and use our XML

```kotlin
override fun generate(dependency: ViewDependency): View {
    val xml = ExampleXml()
    val view = xml.setup(dependency)
    //...
    return view
}
```

First, we create an instance of our layout using `val xml = ExampleXml()`.  This object allows us to access all of the views in our layout that have an identifier, e.g. `android:id="@+id/something"`.  It is not, however, the view itself.  We need to generate the actual view using `val view = xml.setup(dependency)`.

Finally, we return the view we generated after we set up anything else we wish to do.


### Make button clicks activate the increment action

We have a button in our layout with the identifier `increment`, and we need to attach the action to the button.

```kotlin
xml.increment.onClick { increment() }
```

It is recommended that you use `onClick` primarily to just call another function, as it easier to write unit tests.

### Bind the number to the text view

```kotlin
xml.number.bindString(number.map { it.toString() })
```

There are two parts to understand here: `bindString` and `map{}`.

`bindString` takes an `ObservableProperty<String>` and makes sure the text view always shows its current value.  Most view types have multiple `bind` functions that are designed to keep data in sync with the view.

`map` allows us to transform an `ObservableProperty`.  Here we use it to change a number into text for rendering.


### Summary

- `ViewGenerator` is used to bind together a layout with logic to make a functional screen for your app.
- Use `ObservableProperties` as class members to store information.
- Use functions on views starting with `bind` to display/manipulate information.
