# TODO

- [ ] Figure out how to force rebuild Kotlin task
- [ ] Figure out how to clean up files that no longer have meaning
- [ ] Fold fqnames into equivalent files
- [ ] Equivalents embedded inside Android library, side JARs
  - [ ] JS - !Declares -> FQName?
  - [ ] Improve equivalent format
  - [ ] Bring FQnames into equivalents
- [ ] Detekt plugin for equivalents
- [ ] Check compatibility with Gradle XCode plugin
- [ ] Template projects for iOS and Web need updating
- [ ] XML analysis?

## Proposed Changes

- Move equivalents to a folder in 'src' - compiled in into side-archives
- Inline alt-language?


```kotlin

kotlin { doSomePlatformThings() }
js("doSomePlatformThings()")
swift("doSomePlatformThings()")

```