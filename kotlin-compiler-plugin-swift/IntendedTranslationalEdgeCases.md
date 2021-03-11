# Intended Edge Cases in Translation

- Exceptions need to be treated with the level of care they do in swift.
    - Functions that throw need to be marked with the `@Throws` annotation
    - Try/catch must captures `Throwable` to be considered non-throwing
    - Stack traces are not identical
- Generic interfaces don't work as type names.
    - Sequences can't be stored as type names. 
- Print works differently on nullable values.
    - Expect 'Optional(x)'
    - Printing a null results in 'nil'.