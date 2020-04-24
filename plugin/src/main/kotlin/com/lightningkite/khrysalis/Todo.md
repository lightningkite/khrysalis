# Still Needed

- kotlin only/swift only
- full let/also support

```

something.let{ it }.let{ it }.let{ it }

becomes

{ it in { it in { it in it }() }(it) }(something)

This is possible, but fails the clear readability test.

Ideally it just shuffles up to the nearest statement

let it = something
let it2 = it
let it3 = it2
let it4 = it3

Is there a better option?

In typescript, we have this hack

var it = something
var it = it
var it = it
var it = it

Multiplatform Alternative

alternativeLetStyle(something) {}
alternativeAlsoStyle(something) {}



```
