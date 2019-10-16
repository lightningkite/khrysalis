package com.lightningkite.kwift.layout

import com.lightningkite.kwift.utils.*
import kotlin.math.PI

val LayoutConverter.Companion.buttonViews
    get() = LayoutConverter(
        viewTypes = ViewType.mapOf(

            ViewType("SeekBar", "UISlider", "View") { node ->
                node.attributeAsColor("progressTint")?.let {
                    appendln("view.minimumTrackTintColor = $it")
                    appendln("view.maximumTrackTintColor = $it")
                }
                node.attributeAsColor("thumbTint")?.let {
                    appendln("view.thumbTintColor = $it")
                }
            },
            ViewType("com.lightningkite.kwift.views.android.ColorRatingBar", "UIRatingBar", "RatingBar") { node ->
                node.attributeAsColor("app:empty_color")?.let {
                    appendln("view.settings.emptyColor = $it")
                    appendln("view.settings.emptyBorderColor = $it")
                }
                node.attributeAsColor("app:progress_color")?.let {
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
            ViewType("ToggleButton", "ToggleButton", "Button") {},
            ViewType("Switch", "LabeledSwitch", "View") {
                handleCommonText(it, "view.labelView")
            },
            ViewType("Spinner", "Dropdown", "View") {},
            ViewType("RadioGroup", "UIView", "LinearLayout") {},
            ViewType("ImageButton", "UIButton", "Button") { node -> },
            ViewType("Button", "UIButton", "View", handlesPadding = true) { node ->
                node.attributeAsString("android:text")?.let { text ->
                    if (node.attributeAsBoolean("android:textAllCaps") == true) {
                        appendln("view.setTitle($text.toUpperCase(), for: .normal)")
                    } else {
                        appendln("view.setTitle($text, for: .normal)")
                    }
                }
                node.attributeAsColor("android:textColor")?.let {
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


                node.attributeAsImage("android:drawableLeft")?.let { text ->
                    appendln("view.setImage($text, for: .normal)")
                }
                node.attributeAsImage("android:drawableTop")?.let { text ->
                    appendln("view.setImage($text, for: .normal)")
                    appendln("view.addOnLayoutSubviews { [weak view] in")
                    appendln("    view?.setTitlePosition(.bottom)")
                    appendln("}")
                }
                node.attributeAsImage("android:drawableRight")?.let { text ->
                    appendln("view.setImage($text, for: .normal)")
                    appendln("view.addOnLayoutSubviews { [weak view] in")
                    appendln("    view?.setTitlePosition(.left)")
                    appendln("}")
                }
                node.attributeAsImage("android:drawableBottom")?.let { text ->
                    appendln("view.setImage($text, for: .normal)")
                    appendln("view.addOnLayoutSubviews { [weak view] in")
                    appendln("    view?.setTitlePosition(.top)")
                    appendln("}")
                }
                node.attributeAsImage("android:src")?.let { text ->
                    appendln("view.setImage($text, for: .normal)")
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

                handleCommonText(node, "view.titleLabel?")
            }
        )
    )
