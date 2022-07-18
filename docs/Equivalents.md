# Equivalent Files

Khrysalis is just a code translator, and it's easy to handle tasks that can't be translated in each platform individually.  See [code for individual platforms](./platform.md).

Sometimes you need to customize how certain calls are translated.  You can use a *YAML equivalents file* to do the job.

For iOS, an equivalent file is suffixed with `.swift.yaml` and should be contained in the Java project's `src/main/equivalents` folder.

For Web, an equivalent file is suffixed with `.ts.yaml` and should be contained in the Java project's `src/main/equivalents` folder.

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

# A rule that replaces a type used as a value like `MyType`
- id: kotlin.Int
  type: typeRef
  template: "Number" 

# A rule that replaces a getter like `MyType.value`
- id: fully.qualified.name.MyType.value
  type: get
  template: "~this~.getMyValue()" 

# A rule that replaces a setter like `MyType.value = x`
- id: fully.qualified.name.MyType.value
  type: set
  template: "~this~.setMyValue(newValue: ~value~)" 
``` 

## Template Strings

- `raw text` - Any text not within `~` is used literally.
- `~this~` - The receiver (left side of the dot) of the call, get, or set
- `~value~` - The value a variable is being set to
- `~0~` - Numbered parameters start at zero
- `~name~` - Named parameters are usable too
- `~T0~` - Numbered type parameters start at zero
- `~R0~` - A reified type parameter
  - In Swift, this would be `SomeClass<TypeArg>.self`
  - In Typescript, this would be `[SomeClass, TypeArg]`, which is used in the serialization library to identify types for serialization
- `~*~` - All of the arguments separated by commas

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

You can find a mountain of examples over in the [Khrysalis repository](https://github.com/lightningkite/khrysalis) itself - most of the translations are written in this very format.  Some of the most basic translations can be found [here](https://github.com/lightningkite/khrysalis/tree/master/jvm-runtime/src/main/equivalents/).
