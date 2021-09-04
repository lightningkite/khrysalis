package com.lightningkite.khrysalis.ios.layout

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.ios.*

val LayoutConverter.Companion.buttonViews
    get() = LayoutConverter(
        viewTypes = ViewType.mapOf(

            ViewType("SeekBar", "UISlider", "View") { node ->
                setToColor(node, key = "android:progressTint") { it, s ->
                    appendLine("view.minimumTrackTintColor = $it")
                    appendLine("view.maximumTrackTintColor = $it")
                }
                setToColor(node, key = "android:thumbTint") { it, s ->
                    appendLine("view.thumbTintColor = $it")
                }
            },
            ViewType("com.lightningkite.butterfly.views.widget.ColorRatingBar", "UIRatingBar", "RatingBar") { node ->
                setToColor(node, key = "app:empty_color") { it, s ->
                    appendLine("view.settings.emptyColor = $it")
                    appendLine("view.settings.emptyBorderColor = $it")
                }
                setToColor(node, key = "app:progress_color") { it, s ->
                    appendLine("view.settings.filledColor = $it")
                    appendLine("view.settings.filledBorderColor = $it")
                }
            },
            ViewType("RatingBar", "UIRatingBar", "View") { node ->
                node.attributeAsInt("android:numStars")?.let {
                    appendLine("view.settings.totalStars = $it")
                }
                when (node.allAttributes["style"]) {
                    "?android:attr/ratingBarStyle" -> {
                        appendLine("view.settings.starSize = 48")
                    }
                    "?android:attr/ratingBarStyleIndicator" -> {
                        appendLine("view.settings.starSize = 32")
                        appendLine("view.settings.updateOnTouch = false")
                    }
                    "?android:attr/ratingBarStyleSmall" -> {
                        appendLine("view.settings.starSize = 16")
                        appendLine("view.settings.updateOnTouch = false")
                    }
                    else -> {
                        appendLine("view.settings.starSize = 16")
                        appendLine("view.settings.updateOnTouch = false")
                    }
                }
            },
            ViewType("CheckBox", "LabeledCheckbox", "VButton", handlesPadding = true)  { node ->
                node.allAttributes["android:gravity"]?.split('|')?.forEach {
                    when (it) {
                        "top" -> appendLine("view.verticalAlign = .start")
                        "bottom" -> appendLine("view.verticalAlign = .end")
                        "center", "center_vertical" -> appendLine("view.verticalAlign = .center")
                    }
                }
            },
            ViewType("RadioButton", "LabeledRadioButton", "VButton", handlesPadding = true) { node ->
                node.allAttributes["android:gravity"]?.split('|')?.forEach {
                    when (it) {
                        "top" -> appendLine("view.verticalAlign = .start")
                        "bottom" -> appendLine("view.verticalAlign = .end")
                        "center", "center_vertical" -> appendLine("view.verticalAlign = .center")
                    }
                }
            },
            ViewType("Switch", "LabeledSwitch", "VButton", handlesPadding = true) { node ->
                node.allAttributes["android:gravity"]?.split('|')?.forEach {
                    when (it) {
                        "top" -> appendLine("view.verticalAlign = .start")
                        "bottom" -> appendLine("view.verticalAlign = .end")
                        "center", "center_vertical" -> appendLine("view.verticalAlign = .center")
                    }
                }
            },
            ViewType("ToggleButton", "ToggleButton", "Button", handlesPadding = true) { node ->
                node.attributeAsSwiftString("android:textOff")?.let { text ->
                    if (node.attributeAsBoolean("android:textAllCaps") == true) {
                        appendLine("view.textOff = $text.uppercased()")
                        appendLine("view.setTitle($text.uppercased(), for: .normal)")
                    } else {
                        appendLine("view.textOff = $text")
                        appendLine("view.setTitle($text, for: .normal)")
                    }
                }
                node.attributeAsSwiftString("android:textOn")?.let { text ->
                    if (node.attributeAsBoolean("android:textAllCaps") == true) {
                        appendLine("view.textOn = $text.uppercased()")
                    } else {
                        appendLine("view.textOn = $text")
                    }
                }
            },
            ViewType("Spinner", "Dropdown", "VButton", handlesPadding = true) { node ->
                val defaultPadding = node.attributeAsSwiftDimension("android:padding") ?: 0
                append("view.contentEdgeInsets = UIEdgeInsets(top: ")
                append((node.attributeAsSwiftDimension("android:paddingTop") ?: defaultPadding).toString())
                append(", left:")
                append((node.attributeAsSwiftDimension("android:paddingLeft") ?: defaultPadding).toString())
                append(", bottom:")
                append((node.attributeAsSwiftDimension("android:paddingBottom") ?: defaultPadding).toString())
                append(", right:")
                append((node.attributeAsSwiftDimension("android:paddingRight") ?: defaultPadding).toString())
                appendLine(")")
            },
            ViewType("RadioGroup", "LinearLayout", "LinearLayout") {},
            ViewType("ImageButton", "UIButtonWithLayer", "Button", handlesPadding = true) { node -> },
            ViewType("Button", "UIButtonWithLayer", "VButton", handlesPadding = true) { node ->

                node.allAttributes["android:gravity"]?.let { text ->
                    appendLine("view.textGravity = ${align(null, null, text, "center")}")
                }
                node.attributeAsSwiftDimension("android:drawablePadding")?.let { text ->
                    appendLine("view.iconPadding = $text")
                }
                setToColor(node, "android:drawableTint") { it, s ->
                    appendLine("view.iconTint = $it")
                }
                node.attributeAsSwiftLayer("android:drawableLeft", "view")?.let { text ->
                    appendLine("view.iconPosition = .left")
                    appendLine("view.iconLayer = $text")
                }
                node.attributeAsSwiftLayer("android:drawableTop", "view")?.let { text ->
                    appendLine("view.iconPosition = .top")
                    appendLine("view.iconLayer = $text")
                }
                node.attributeAsSwiftLayer("android:drawableRight", "view")?.let { text ->
                    appendLine("view.iconPosition = .right")
                    appendLine("view.iconLayer = $text")
                }
                node.attributeAsSwiftLayer("android:drawableBottom", "view")?.let { text ->
                    appendLine("view.iconPosition = .bottom")
                    appendLine("view.iconLayer = $text")
                }
                node.attributeAsSwiftLayer("android:src", "view")?.let { text ->
                    appendLine("view.iconPosition = .center")
                    appendLine("view.iconLayer = $text")
                }
            },
            ViewType("VButton", "UIButton", "View", handlesPadding = true) { node ->
                handleCommonText(node, "view.titleLabel?", controlView = "view")

                node.attributeAsSwiftDimension("android:letterSpacing")?.let {
                    appendLine("view.letterSpacing = $it")
                }
                node.attributeAsBoolean("android:textAllCaps")?.let {
                    appendLine("view.textAllCaps = $it")
                }
                node.attributeAsSwiftString("android:text")?.let { text ->
                    appendLine("view.textString = $text")
                }
                setToColor(node, "android:textColor") { it, s ->
                    appendLine("view.setTitleColor($it, for: $s)")
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
                        appendLine("view.contentHorizontalAlignment = $fixed")
                    }

                appendLine(
                    "view.contentMode = ${
                        when (node.allAttributes["android:scaleType"]) {
                            "fitXY" -> ".scaleToFill"
                            "centerCrop" -> ".scaleAspectFill"
                            "centerInside" -> ".scaleAspectFit"
                            else -> ".scaleAspectFit"
                        }
                    }"
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
                appendLine(")")
            }
        )
    )
