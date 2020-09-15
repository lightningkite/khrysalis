# Equivalent Files

Khrysalis is just a code translator, and it's easy to handle tasks that can't be translated in each platform individually.  See [code for individual platforms](./platform.md).

Sometimes, however, it's nicer to just specify how a certain construct is translated to the other languages so you don't have to rewrite it every time.  Sometimes you just want the code on the other side to look more native.  In either case, you can use a *YAML equivalents file* to do the job.

For iOS, an equivalent file is suffixed with `.swift.yaml` and can be placed anywhere in your iOS project folder.

For Web, an equivalent file is suffixed with `.ts.yaml` and can be placed anywhere in your web project folder.

In either case, the example of one of these equivalent files looks like this:

```yaml
---
# A rule that replaces a function call like `someFunction(0)`
- id: fully.qualified.name.someFunction
  type: call
  template: "equivalentFunctionInSwift(argName: ~0~)"

# A rule that replaces a type like `MyType<Int>`
- id: fully.qualified.name.MyType
  type: type
  template: "SwiftType<~T0~>" 

# A rule that replaces a getter like `MyType.value`
- id: fully.qualified.name.MyType.value
  type: get
  template: "~this~.getMyValue()" 

# A rule that replaces a setter like `MyType.value = x`
- id: fully.qualified.name.MyType.value
  type: get
  template: "~this~.setMyValue(newValue: ~value~)" 
``` 

## Imports

### iOS / Swift

When translating to Swift, sometimes you also need to import something from another module.  You can do so like this:

```yaml
- id: fully.qualified.name.MyType
  type: type
  template: 
    pattern: "SwiftType<~T0~>"
    imports: ["ModuleName"] 
```

### Web / Typescript

When translating to Typescript, imports are a bit more complicated.

```yaml
- id: fully.qualified.name.MyType
  type: type
  template: 
    pattern: "TypescriptType<~T0~>"
    imports:
      TypescriptType: path/to/file 
      # Or, alternatively:
      RenamedThing: ActualName from path/to/file 
```

## Examples

You can find a mountain of examples over in the [Khrysalis repository](https://github.com/lightningkite/khrysalis) itself - most of the translations are written in this very format.  Some of the most basic translations for iOS can be found [here](https://github.com/lightningkite/khrysalis/tree/master/ios/Khrysalis/src/kotlin) and the most basic translations for Web can be found [here](https://github.com/lightningkite/khrysalis/tree/master/web/src/kotlin).