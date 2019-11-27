package com.lightningkite.kwift.layout

import com.lightningkite.kwift.utils.*

val LayoutConverter.Companion.buttonViews
    get() = LayoutConverter(
        viewTypes = ViewType.mapOf(

            ViewType("SeekBar", "UISlider", "View") { node ->
                node.setToColorGivenControl(key = "android:progressTint") {
                    appendln("view.minimumTrackTintColor = $it")
                    appendln("view.maximumTrackTintColor = $it")
                }
                node.setToColorGivenControl(key = "android:thumbTint") {
                    appendln("view.thumbTintColor = $it")
                }
            },
            ViewType("com.lightningkite.kwift.views.android.ColorRatingBar", "UIRatingBar", "RatingBar") { node ->
                node.setToColorGivenControl(key = "app:empty_color") {
                    appendln("view.settings.emptyColor = $it")
                    appendln("view.settings.emptyBorderColor = $it")
                }
                node.setToColorGivenControl(key = "app:progress_color") {
                    appendln("view.settings.filledColor = $it")
                    appendln("view.settings.filledBorderColor = $it")
                }
            },
            ViewType("RatingBar", "UIRatingBar", "View") { node ->
                node.attributeAsInt("android:numStars")?.let {
                    appendln("view.settings.totalStars = $it")
                }
                when (node.attributes["style"]) {
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
            ViewType("CheckBox", "LabeledCheckbox", "View") {
                handleCommonText(it, "view.labelView")
            },
            ViewType("RadioButton", "LabeledRadioButton", "View") {
                handleCommonText(it, "view.labelView")
            },
            ViewType("ToggleButton", "ToggleButton", "Button") { node ->
                node.attributeAsString("android:textOff")?.let { text ->
                    if (node.attributeAsBoolean("android:textAllCaps") == true) {
                        appendln("view.textOff = $text.toUpperCase()")
                        appendln("view.setTitle($text.toUpperCase(), for: .normal)")
                    } else {
                        appendln("view.textOff = $text")
                        appendln("view.setTitle($text, for: .normal)")
                    }
                }
                node.attributeAsString("android:textOn")?.let { text ->
                    if (node.attributeAsBoolean("android:textAllCaps") == true) {
                        appendln("view.textOn = $text.toUpperCase()")
                    } else {
                        appendln("view.textOn = $text")
                    }
                }
            },
            ViewType("Switch", "LabeledSwitch", "View") {
                handleCommonText(it, "view.labelView", controlView = "view.control")
            },
            ViewType("Spinner", "Dropdown", "View") {},
            ViewType("RadioGroup", "UIView", "LinearLayout") {},
            ViewType("ImageButton", "UIButtonWithLayer", "Button") { node -> },
            ViewType("Button", "UIButtonWithLayer", "View", handlesPadding = true) { node ->
                handleCommonText(node, "view.titleLabel?", controlView = "view")

                node.attributeAsDimension("android:letterSpacing")?.let {
                    appendln("view.letterSpacing = $it")
                }
                node.attributeAsBoolean("android:textAllCaps")?.let {
                    appendln("view.textAllCaps = $it")
                }
                node.attributeAsString("android:text")?.let { text ->
                    appendln("view.textString = $text")
                }
                node.setToColorGivenControl("android:textColor") {
                    appendln("view.setTitleColor($it, for: .normal)")
                }

                node.attributes["android:gravity"]
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


                node.attributes["android:gravity"]?.let { text ->
                    appendln("view.textGravity = ${align(null, null, text, "center")}")
                }
                node.attributeAsDimension("android:drawablePadding")?.let { text ->
                    appendln("view.iconPadding = $text")
                }
                node.attributeAsLayer("android:drawableLeft", "view")?.let { text ->
                    appendln("view.iconPosition = .left")
                    appendln("view.iconLayer = $text")
                }
                node.attributeAsLayer("android:drawableTop", "view")?.let { text ->
                    appendln("view.iconPosition = .top")
                    appendln("view.iconLayer = $text")
                }
                node.attributeAsLayer("android:drawableRight", "view")?.let { text ->
                    appendln("view.iconPosition = .right")
                    appendln("view.iconLayer = $text")
                }
                node.attributeAsLayer("android:drawableBottom", "view")?.let { text ->
                    appendln("view.iconPosition = .bottom")
                    appendln("view.iconLayer = $text")
                }
                node.attributeAsLayer("android:src", "view")?.let { text ->
                    appendln("view.iconPosition = .top")
                    appendln("view.iconLayer = $text")
                }
                appendln(
                    "view.contentMode = ${when (node.attributes["android:scaleType"]) {
                        "fitXY" -> ".scaleToFill"
                        "centerCrop" -> ".scaleAspectFill"
                        "centerInside" -> ".scaleAspectFit"
                        else -> ".scaleAspectFit"
                    }}"
                )

                val defaultPadding = node.attributeAsDimension("android:padding") ?: 0
                append("view.contentEdgeInsets = UIEdgeInsets(top: ")
                append((node.attributeAsDimension("android:paddingTop") ?: defaultPadding).toString())
                append(", left:")
                append((node.attributeAsDimension("android:paddingLeft") ?: defaultPadding).toString())
                append(", bottom:")
                append((node.attributeAsDimension("android:paddingBottom") ?: defaultPadding).toString())
                append(", right:")
                append((node.attributeAsDimension("android:paddingRight") ?: defaultPadding).toString())
                appendln(")")
            }
        )
    )
