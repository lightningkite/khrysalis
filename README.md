# Khrysalis

Gradle plugin to translate basic Kotlin to Swfit.

Don't expect this to work perfectly under every circumstance.  It won't.

You can configure it like this:

```groovy
khrysalis {
    inputDirectory = project.file("")
    outputDirectory = project.file("")
}
```
