# Rapid Prototyping in Khrysalis

Khrysalis comes with a rapid-prototyping system as a simple extension to existing Android layout XML.

Essentially, it takes what the XML previewer shows and makes it runnable, adding some fairly complex navigation options to the `tools` namespace.

The following tags can now be used to describe navigational behavior:

- `tools:goTo="@layout/other_view"`
    - Tapping this view takes the user to another screen, adding it to the stack.
- `tools:swap="@layout/other_view"`
    - Tapping this view takes the user to another screen, replacing this one.
- `tools:pop="@layout/other_view"`
    - Tapping this view takes the user back one screen.
- `tools:dismiss="@layout/other_view"`
    - Tapping this view takes the user back one screen, but allows for empty stacks.  Useful for overlays.
- `tools:reset="@layout/other_view"`
    - Tapping this view takes the user to another screen, emptying the whole stack. 
- `tools:popTo="@layout/other_view"`
    - Tapping this view takes the user back to a particular screen.
- `tools:onStack="myStack"`
    - Rather than performing any of the operations above on the default stack, do it on this stack instead.
- `tools:stackId="myStack"`
    - Sets a `com.lightningkite.khrysalis.views.android.SwapView` to display a newly-created stack, whose name is defined here.
- `tools:stackDefault`
    - Defines what view to start with when present on the above command.
- `tools:requires`
    - Defines a variable that this layout requires to work.
- `tools:provides`
    - Defines a variable that this layout passes to other views.

The `ViewGenerator`s created by the prototyper will have section comments.  Each section will initially have `(overwritten on flow generation)` appended to it, which indicates that the contents of the section will be overwritten if the prototyper is run again.  Removing that message will leave the section as is, allowing you to customize behavior after generation.

Sections are defined as starting at the comment like `//--- My Section` and ending at the next section comment.  Unrecognized sections without the overwrite instruction will be left alone.

## Flow

In addition to generating the backing code to demonstrate your layouts, the `khrysalisFlowDoc` task generates multiple diagrams showing how your user moves around the app.
