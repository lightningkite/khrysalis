# Layouts

Layouts in Khrysalis are made using standard Android XML layouts.  For more information on that, take a look [here](https://developer.android.com/guide/topics/ui/declaring-layout).  We'll continue assuming you know how Android XML layouts work already.

Khrysalis supports a large subset of Android views, with the notable exceptions of `RelativeLayout` and `ConstraintLayout`.

- Layouts
    - `LinearLayout`
    - `FrameLayout`
    - `RecyclerView`
    - `ScrollView`
    - `HorizontalScrollView`
    - `ViewFlipper`
    - `RadioGroup`
- Basic Widgets
    - `TextView`
    - `EditText`
    - `Button`
    - `Switch`
    - `RadioButton`
    - `CheckBox`
    - `ImageView`
    - `ImageButton`
    - `SeekBar`
    - `Spinner`
    - `ProgressBar`
    - `AutoCompleteTextView`
    - `CompoundButton`
    - `RatingBar`
    - `ToggleButton`
- 3rd Party Widgets
    - `RecyclerView`
    - `androidx.viewpager.widget.ViewPager`
    - `com.google.android.material.tabs.TabLayout`
    - `com.lightningkite.khrysalis.views.VideoPlayer`
    - `com.google.android.gms.maps.MapView`
    - `com.rd.PageIndicatorView`
    
In addition to those widgets, there are some Khrysalis-specific widgets you can use:

- `com.lightningkite.khrysalis.views.android.HorizontalProgressBar`
    - An actual progress bar, rather than just a spinner.  If you wish to use a full, determinate progress bar, you need to use this instead of the standard `ProgressBar` so that the layout translator can translate it to a more proper equivalent.
- `com.lightningkite.khrysalis.views.android.MultilineEditText`
    - An text field with multiple lines.  Used to distinguish between multi-line and single-line because other platforms have different views for each. 
- `com.lightningkite.khrysalis.views.android.DateButton`
    - A button that acts as a date field, allowing the user to see and change a date.
- `com.lightningkite.khrysalis.views.android.TimeButton`
    - A button that acts as a time field, allowing the user to see and change a time.
- `com.lightningkite.khrysalis.views.android.ColorRatingBar`
    - A rating bar that can be given an alternative color.
- `com.lightningkite.khrysalis.views.android.VerticalRecyclerView`
    - A recycler view that explictly has vertical scroll bars.
- `com.lightningkite.khrysalis.views.CustomView`
    - A view that has custom interactions, such as rendering and tap responses.  The delegate you give it can be translated across all platforms.
- `com.lightningkite.khrysalis.views.android.SwapView`
    - A view that swaps between other views, usually using a stack.