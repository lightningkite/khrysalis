Strategy

Non-mutable, use Array
Mutable, use WrappedArray to make let work

Always use wrapped

Always use var

TYPE CONDITIONS
- 'var' over 'let' on properties
- 'inout' and '&' on function parameters
- Absolutely broken on constructors, though, as it will copy at that point and no longer modify.

LIMITATIONS UNDER TYPE CONDITIONS
- Anything depending on the usage of lists as by-reference outside of a single function will fail.
- Casting to a mutable is dangerous.

THIS IS WORTH IT?!

Maybe.  It doesn't guarantee code will work, but it is *much* more Swifty.
You probably shouldn't be doing anything that requires references anyways.

-----------

- Support immutable variables relatively safely with Lists
- Support Operator Overloading
- Support generic protocols as fields via boxing
- Support Sequence<T> as LazySequence
    - Will require the boxing above
- Use `struct` for data classes?
- Support psuedo-hashability by reference on objects
    - `ObjectIdentifier`
- Support exception handling via @Throws annotation
- Support extending exception
    - Errors don't need to be enums, they can be structs!
- Support try/catch

```swift
//NOT EXPRESSIONS DANG IT
run {
    do {
        return try parse(myXMLData)
    } catch let e as XMLParsingError {
        print("Parsing error: \(e.kind) [\(e.line):\(e.column)]")
    } catch {
        print("Other error: \(error)")
    }
}
```

- Support psuedo-hashability of lambdas?  Might not be possible at all...