# Platform-Specific Code

Writing platform-specific code is easy because Khrysalis is *literally just a translator*.  To write platform-specific code, simply put it in a file that isn't shared code.

Here's a simple example, first with the platform specific code files.

```kotlin
// platform.actual.kt
package com.lightningkite.khrysalis.example
fun printPlatform(){
    println("Hello from Android!")
}
```

```swift
// platform.actual.swift
func printPlatform() {
    print("Hello from iOS!")
}
```

```typescript
// platform.actual.ts
// The comment below is needed to indicate to the transpiler where this function is found.
// Format: 
// - an exclamation mark immediately following the comment
// - a space
// - the word 'Declares'
// - a space
// - and the fully qualified name of the function.
//! Declares com.lightningkite.khrysalis.example.printPlatform
function printPlatform() {
    console.log("Hello from Web!")
}
```

And here's an example of using that function from shared code now:

```kotlin
// test.shared.kt
fun printAppInformation(){
    printPlatform()
}
```

That's literally it.  It's designed to be extremely unmagical.  You can use platform-specific libraries as much as you please within these files.

If you want to make some code translate differently, for example, change the name of the function on the other platform, take a look at the [equivalents tutorial](./Equivalents.md).