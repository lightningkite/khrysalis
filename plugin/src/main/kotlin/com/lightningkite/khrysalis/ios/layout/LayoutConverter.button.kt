package com.lightningkite.khrysalis.ios.layout

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.ios.*

val LayoutConverter.Companion.buttonViews
    get() = LayoutConverter(
        viewTypes = ViewType.mapOf(

            ViewType("SeekBar", "UISlider", "View") { node ->
                setToColor(node, key = "android:progressTint") { it, s ->
                    appendln("view.minimumTrackTintColor = $it")
                    appendln("view.maximumTrackTintColor = $it")
                }
                setToColor(node, key = "android:thumbTint") { it, s ->
                    appendln("view.thumbTintColor = $it")
                }
            },
            ViewType("com.lightningkite.khrysalis.views.android.ColorRatingBar", "UIRatingBar", "RatingBar") { node ->
                setToColor(node, key = "app:empty_color") { it, s ->
                    appendln("view.settings.emptyColor = $it")
                    appendln("view.settings.emptyBorderColor = $it")
                }
                setToColor(node, key = "app:progress_color") { it, s ->
                    appendln("view.settings.filledColor = $it")
                    appendln("view.settings.filledBorderColor = $it")
                }
            },
            ViewType("RatingBar", "UIRatingBar", "View") { node ->
                node.attributeAsInt("android:numStars")?.let {
                    appendln("view.settings.totalStars = $it")
                }
                when (node.allAttributes["style"]) {
                    "?android:attr/ratingBarStyle" -> {
                        appendln("view.settings.starSize = 48")
                    }
                    "?android:attr/ratingBarStyleIndicator" -> {
                        appendln("view.settings.starSize = 32")
                        appendln("view.settings.updateOnTouch = false")
                    }
                    "?android:attr/ratingBarStyleSmall" -> {
                        appendln("view.settings.starSize = 16")
                        appendln("view.settings.updateOnTouch = false")
                    }
                    else -> {
                        appendln("view.settings.starSize = 16")
                        appendln("view.settings.updateOnTouch = false")
                    }
                }
            },
            ViewType("CheckBox", "LabeledCheckbox", "View")  { node ->
                node.allAttributes["android:gravity"]?.split('|')?.forEach {
                    when(it){
                        "top" -> appendln("view.verticalAlign = .start")
                        "bottom" -> appendln("view.verticalAlign = .end")
                        "center", "center_vertical" -> appendln("view.verticalAlign = .center")
                    }
                }
                handleCommonText(node, "view.labelView", checkView = "view")
            },
            ViewType("RadioButton", "LabeledRadioButton", "View") { node ->
                node.allAttributes["android:gravity"]?.split('|')?.forEach {
                    when(it){
                        "top" -> appendln("view.verticalAlign = .start")
                        "bottom" -> appendln("view.verticalAlign = .end")
                        "center", "center_vertical" -> appendln("view.verticalAlign = .center")
                    }
                }
                handleCommonText(node, "view.labelView", checkView = "view")
            },
            ViewType("Switch", "LabeledSwitch", "View") { node ->
                node.allAttributes["android:gravity"]?.split('|')?.forEach {
                    when(it){
                        "top" -> appendln("view.verticalAlign = .start")
                        "bottom" -> appendln("view.verticalAlign = .end")
                        "center", "center_vertical" -> appendln("view.verticalAlign = .center")
                    }
                }
                handleCommonText(node, "view.labelView", controlView = "view.control")
            },
            ViewType("ToggleButton", "ToggleButton", "Button", handlesPadding = true) { node ->
                node.attributeAsSwiftString("android:textOff")?.let { text ->
                    if (node.attributeAsBoolean("android:textAllCaps") == true) {
                        appendln("view.textOff = $text.uppercased()")
                        appendln("view.setTitle($text.uppercased(), for: .normal)")
                    } else {
                        appendln("view.textOff = $text")
                        appendln("view.setTitle($text, for: .normal)")
                    }
                }
                node.attributeAsSwiftString("android:textOn")?.let { text ->
                    if (node.attributeAsBoolean("android:textAllCaps") == true) {
                        appendln("view.textOn = $text.uppercased()")
                    } else {
                        appendln("view.textOn = $text")
                    }
                }
            },
            ViewType("Spinner", "Dropdown", "View", handlesPadding = true) { node ->
                val defaultPadding = node.attributeAsSwiftDimension("android:padding") ?: 0
                append("view.contentEdgeInsets = UIEdgeInsets(top: ")
                append((node.attributeAsSwiftDimension("android:paddingTop") ?: defaultPadding).toString())
                append(", left:")
                append((node.attributeAsSwiftDimension("android:paddingLeft") ?: defaultPadding).toString())
                append(", bottom:")
                append((node.attributeAsSwiftDimension("android:paddingBottom") ?: defaultPadding).toString())
                append(", right:")
                append((node.attributeAsSwiftDimension("android:paddingRight") ?: defaultPadding).toString())
                appendln(")")
            },
            ViewType("RadioGroup", "LinearLayout", "LinearLayout") {},
            ViewType("ImageButton", "UIButtonWithLayer", "Button", handlesPadding = true) { node -> },
            ViewType("Button", "UIButtonWithLayer", "View", handlesPadding = true) { node ->
                handleCommonText(node, "view.titleLabel?", controlView = "view")

                node.attributeAsSwiftDimension("android:letterSpacing")?.let {
                    appendln("view.letterSpacing = $it")
                }
                node.attributeAsBoolean("android:textAllCaps")?.let {
                    appendln("view.textAllCaps = $it")
                }
                node.attributeAsSwiftString("android:text")?.let { text ->
                    appendln("view.textString = $text")
                }
                setToColor(node, "android:textColor") { it, s ->
                    appendln("view.setTitleColor($it, for: $s)")
                }

                node.allAttributes["android:gravity"]
                    ?.split('|')
                    ?.asSequence()
                    ?.mapNotNull { horizontalGravityWords[it] }
                    ?.firstOrNull()
                    .let {
                        val fixed = when (it) {
                            ".start" -> ".left"
                            ".center" -> ".center"
                            ".end" -> ".right"
                            else -> ".center"
                        }
                        appendln("view.contentHorizontalAlignment = $fixed")
                    }


                node.allAttributes["android:gravity"]?.let { text ->
                    appendln("view.textGravity = ${align(null, null, text, "center")}")
                }
                node.attributeAsSwiftDimension("android:drawablePadding")?.let { text ->
                    appendln("view.iconPadding = $text")
                }
                setToColor(node, "android:drawableTint") { it, s ->
                    appendln("view.iconTint = $it")
                }
                node.attributeAsSwiftLayer("android:drawableLeft", "view")?.let { text ->
                    appendln("view.iconPosition = .left")
                    appendln("view.iconLayer = $text")
                }
                node.attributeAsSwiftLayer("android:drawableTop", "view")?.let { text ->
                    appendln("view.iconPosition = .top")
                    appendln("view.iconLayer = $text")
                }
                node.attributeAsSwiftLayer("android:drawableRight", "view")?.let { text ->
                    appendln("view.iconPosition = .right")
                    appendln("view.iconLayer = $text")
                }
                node.attributeAsSwiftLayer("android:drawableBottom", "view")?.let { text ->
                    appendln("view.iconPosition = .bottom")
                    appendln("view.iconLayer = $text")
                }
                node.attributeAsSwiftLayer("android:src", "view")?.let { text ->
                    appendln("view.iconPosition = .top")
                    appendln("view.iconLayer = $text")
                }
                appendln(
                    "view.contentMode = ${when (node.allAttributes["android:scaleType"]) {
                        "fitXY" -> ".scaleToFill"
                        "centerCrop" -> ".scaleAspectFill"
                        "centerInside" -> ".scaleAspectFit"
                        else -> ".scaleAspectFit"
                    }}"
                )

                val defaultPadding = node.attributeAsSwiftDimension("android:padding") ?: 0
                append("view.contentEdgeInsets = UIEdgeInsets(top: ")
                append((node.attributeAsSwiftDimension("android:paddingTop") ?: defaultPadding).toString())
                append(", left:")
                append((node.attributeAsSwiftDimension("android:paddingLeft") ?: defaultPadding).toString())
                append(", bottom:")
                append((node.attributeAsSwiftDimension("android:paddingBottom") ?: defaultPadding).toString())
                append(", right:")
                append((node.attributeAsSwiftDimension("android:paddingRight") ?: defaultPadding).toString())
                appendln(")")
            }
        )
    )
