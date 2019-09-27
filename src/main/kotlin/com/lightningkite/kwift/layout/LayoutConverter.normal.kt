package com.lightningkite.kwift.layout

import com.lightningkite.kwift.utils.*

val LayoutConverter.Companion.normal get() = LayoutConverter(
    viewTypes = ViewType.mapOf(
        ViewType("View", "UIView") { node ->
            node.attributes["android:id"]?.let { raw ->
                val name = raw.removePrefix("@+id/").removePrefix("@id/").camelCase()
                appendln("self.$name = view")
                bindings[name] = converter.viewTypes[node.name]?.iosName ?: "UIView"
            }
            node.attributes["android:background"]?.let { raw ->
                when {
                    raw.startsWith("@drawable/") -> {
                        node.attributeAsLayer("android:background", "view")!!.let {
                            appendln("view.backgroundLayer = $it")
                        }
                    }
                    raw.startsWith("@mipmap/") -> {
                        val drawableName = raw.removePrefix("@mipmap/")
                        appendln("if let image = UIImage(named: \"$drawableName\") {")
                        appendln("view.backgroundColor = UIColor(patternImage: image)")
                        appendln("}")
                    }
                    raw.startsWith("@color/") -> {
                        val colorName = raw.removePrefix("@color/")
                        appendln("view.backgroundColor = ResourcesColors.${colorName.camelCase()}")
                    }
                    raw.startsWith("@android:color/") -> {
                        val colorName = raw.removePrefix("@android:color/")
                        appendln("view.backgroundColor = ResourcesColors.${colorName.camelCase()}")
                    }
                    else -> {
                    }
                }
            }
            node.attributeAsDimension("android:elevation")?.let {
                appendln("view.layer.masksToBounds = false")
                appendln("view.layer.shadowColor = UIColor.black.cgColor")
                appendln("view.layer.shadowOffset = CGSize(width: 0, height: $it)")
                appendln("view.layer.shadowRadius = $it")
                appendln("view.layer.shadowOpacity = 0.24")
            }

            val defaultPadding = node.attributeAsDimension("android:padding") ?: 0
            val paddingTop = (node.attributeAsDimension("android:paddingTop") ?: defaultPadding)
            val paddingLeft = (node.attributeAsDimension("android:paddingLeft") ?: defaultPadding)
            val paddingBottom = (node.attributeAsDimension("android:paddingBottom") ?: defaultPadding)
            val paddingRight = (node.attributeAsDimension("android:paddingRight") ?: defaultPadding)
            appendln("view.layoutMargins = UIEdgeInsets(top: $paddingTop, left: $paddingLeft, bottom: $paddingBottom, right: $paddingRight)")

        },


        ViewType("com.lightningkite.kwift.views.android.TimeButton", "TimeButton", "Button") {},
        ViewType("com.lightningkite.kwift.views.android.DateButton", "DateButton", "Button") {},
        ViewType("com.lightningkite.kwift.views.android.PreviewVariedFlipper", "PreviewVariedFlipper", "FrameLayout") {},
        ViewType("androidx.recyclerview.widget.RecyclerView", "UITableView", "View") {},
        ViewType("Space", "UIView", "View") {},


        ViewType("ImageView", "UIImageView", "View") { node ->
            node.attributeAsImage("android:src")?.let { text ->
                val defaultPadding = node.attributeAsDimension("android:padding") ?: 0
                val paddingTop = (node.attributeAsDimension("android:paddingTop") ?: defaultPadding)
                val paddingLeft = (node.attributeAsDimension("android:paddingLeft") ?: defaultPadding)
                val paddingBottom = (node.attributeAsDimension("android:paddingBottom") ?: defaultPadding)
                val paddingRight = (node.attributeAsDimension("android:paddingRight") ?: defaultPadding)
                appendln("view.image = $text?.withInset(insets: UIEdgeInsets(top: $paddingTop, left: $paddingLeft, bottom: $paddingBottom, right: $paddingRight))")
            }
            appendln(
                "view.contentMode = ${when (node.attributes["android:scaleType"]) {
                    "fitXY" -> ".scaleToFill"
                    "centerCrop" -> ".scaleAspectFill"
                    "centerInside" -> ".scaleAspectFit"
                    else -> ".scaleAspectFit"
                }}"
            )
        },
        ViewType("de.hdodenhof.circleimageview.CircleImageView", "UIImageView", "ImageView") { node ->

            appendln("view.addOnLayoutSubviews { [weak view] in")
            appendln("if let view = view {")
            appendln("    view.layer.cornerRadius = view.frame.size.width / 2;")
            appendln("}")
            appendln("}")
            appendln("view.clipsToBounds = true")
        },


        ViewType("TextView", "UILabel", "View") { node ->
            handleCommonText(node)
        },


        ViewType("ScrollView", "UIScrollView", "View") { node ->
            val child = node.children.first()
            appendln("view.flex.direction(.column).alignContent(.center).addItem({")
            append("let sub = ")
            write(child)
            appendln()
            appendln("let dg = ScrollSavingDelegate()")
            appendln("view.delegate = dg")
            appendln("view.addOnLayoutSubviews { [weak view, weak sub] in")
            appendln("if let view = view, let sub = sub {")
            appendln("    view.contentSize = sub.frame.size")
            appendln("    view.contentOffset = dg.lastNonzeroOffset")
            appendln("}")
            appendln("}")
            appendln("")
            appendln("return sub")
            appendln("}()")

            if (node.attributes["android:fillViewport"] == "true") {
                append(").shrink(0).grow(1)")
            } else {
                append(").shrink(0)")
            }
            child.attributeAsDimension("android:layout_width")?.let { s ->
                append(".width($s)")
            }
            child.attributeAsDimension("android:layout_height")?.let { s ->
                append(".height($s)")
            }
            child.attributeAsDimension("android:minWidth")?.let { s ->
                append(".minWidth($s)")
            }
            child.attributeAsDimension("android:minHeight")?.let { s ->
                append(".minHeight($s)")
            }
            appendln()
        },

        ViewType("HorizontalScrollView", "UIScrollView", "View") { node ->
            val child = node.children.first()
            appendln("view.flex.direction(.row).alignContent(.center).addItem({")
            append("let sub = ")
            write(child)
            appendln()
            appendln("view.flexFix(sub)")
            appendln()
            appendln("return sub")
            appendln("}()")

            if (node.attributes["android:fillViewport"] == "true") {
                append(").shrink(0).grow(1)")
            } else {
                append(").shrink(0)")
            }
            child.attributeAsDimension("android:layout_width")?.let { s ->
                append(".width($s)")
            }
            child.attributeAsDimension("android:layout_height")?.let { s ->
                append(".height($s)")
            }
            child.attributeAsDimension("android:minWidth")?.let { s ->
                append(".minWidth($s)")
            }
            child.attributeAsDimension("android:minHeight")?.let { s ->
                append(".minHeight($s)")
            }
            appendln()
        },


        ViewType("com.lightningkite.kwift.views.android.MultilineEditText", "UITextView", "EditText") { node ->
            //Purposefully empty
        },
        ViewType("com.rd.PageIndicatorView", "UIPageControl", "View") { node ->
            node.attributeAsColor("app:piv_selectedColor")?.let {
                appendln("view.currentPageIndicatorTintColor = $it")
            }
            node.attributeAsColor("app:piv_unselectedColor")?.let {
                appendln("view.pageIndicatorTintColor = $it")
            }
        },
        ViewType("androidx.viewpager.widget.ViewPager", "UICollectionView", "View") { node ->
        },
        ViewType("com.google.android.material.tabs.TabLayout", "UISegmentedControl", "View") { node ->
            appendln("view.tintColor = .clear")
            appendln("view.backgroundColor = .clear")

            node.attributes["app:tabMode"]?.let {
                when (it) {
                    "scrollable" -> appendln("view.apportionsSegmentWidthsByContent = true")
                    "fixed" -> appendln("view.apportionsSegmentWidthsByContent = false")
                    else -> appendln("view.apportionsSegmentWidthsByContent = false")
                }
            }
            node.attributes["app:tabGravity"]?.let {
                when (it) {
                    "center" -> appendln("view.apportionsSegmentWidthsByContent = true")
                    "fill" -> appendln("view.apportionsSegmentWidthsByContent = false")
                    else -> appendln("view.apportionsSegmentWidthsByContent = false")
                }
            }
            node.attributeAsColor("app:tabBackground")?.let {
            }
            node.attributeAsColor("app:tabRippleColor")?.let {
            }
            (node.attributeAsColor("app:tabTextColor") ?: "UIColor.black").let {
                appendln(
                    """view.setTitleTextAttributes(
                [NSAttributedString.Key.foregroundColor: $it], 
                for: .normal
                )"""
                )
            }
            (node.attributeAsColor("app:tabTextColor") ?: "ResourcesColors.colorPrimary").let {
                appendln(
                    """view.setTitleTextAttributes(
                [NSAttributedString.Key.foregroundColor: $it], 
                for: .selected
                )"""
                )
            }
            (node.attributeAsColor("app:tabTextColor") ?: "ResourcesColors.colorPrimary").let {
                appendln("view.addIndicator(color: $it)")
            }
        },
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
        ViewType("com.google.android.gms.maps.MapView", "MKMapView", "View") { node ->

        },
        ViewType("com.lightningkite.kwift.views.android.WeekView", "UIWeekView", "View") { node ->
            //TODO
        },
        ViewType("com.lightningkite.kwift.views.android.CalendarView", "CalendarView", "View") { node ->

            node.attributeAsColor("app:selectedForegroundColor")?.let {
                appendln("view.selectedColorSet.foreground = $it")
            }
            node.attributeAsColor("app:selectedBackgroundColor")?.let {
                appendln("view.selectedColorSet.background = $it")
            }
            node.attributeAsColor("app:defaultForegroundColor")?.let {
                appendln("view.defaultColorSet.foreground = $it")
            }
            node.attributeAsColor("app:defaultBackgroundColor")?.let {
                appendln("view.defaultColorSet.background = $it")
            }
            node.attributeAsColor("app:labelForegroundColor")?.let {
                appendln("view.labelColorSet.foreground = $it")
            }
            node.attributeAsColor("app:labelBackgroundColor")?.let {
                appendln("view.labelColorSet.background = $it")
            }

            node.attributeAsDimension("app:headerFont")?.let {
                appendln("view.headerFont = $it")
            }
            node.attributeAsDimension("app:labelFont")?.let {
                appendln("view.labelFont = $it")
            }
            node.attributeAsDimension("app:dayFont")?.let {
                appendln("view.dayFont = $it")
            }

            node.attributeAsDimension("app:internalPadding")?.let {
                appendln("view.internalPadding = $it")
            }
            node.attributeAsDimension("app:dayCellMargin")?.let {
                appendln("view.dayCellMargin = $it")
            }

            node.attributeAsString("app:leftText")?.let {
                appendln("view.leftText = $it")
            }
            node.attributeAsString("app:rightText")?.let {
                appendln("view.rightText = $it")
            }
        },
        ViewType(
            "com.lightningkite.kwift.views.android.SelectDateRangeView",
            "SelectDateRangeView",
            "com.lightningkite.kwift.views.android.CalendarView"
        ) { node ->

        },
        ViewType(
            "com.lightningkite.kwift.views.android.SelectDayView",
            "SelectDayView",
            "com.lightningkite.kwift.views.android.CalendarView"
        ) { node ->

        },
        ViewType("EditText", "UITextField", "View") { node ->
            val defaultPadding = node.attributeAsDimension("android:padding") ?: 0
            val paddingTop = (node.attributeAsDimension("android:paddingTop") ?: defaultPadding)
            val paddingLeft = (node.attributeAsDimension("android:paddingLeft") ?: defaultPadding)
            val paddingBottom = (node.attributeAsDimension("android:paddingBottom") ?: defaultPadding)
            val paddingRight = (node.attributeAsDimension("android:paddingRight") ?: defaultPadding)

            if (paddingLeft != 0) {
                appendln("view.setLeftPaddingPoints($paddingLeft)")
            }
            if (paddingRight != 0) {
                appendln("view.setRightPaddingPoints($paddingRight)")
            }

            node.attributeAsString("android:hint")?.let { text ->
                appendln("view.placeholder = $text")
            }
            node.attributes["android:inputType"]?.let { type ->
                appendln("view.autocapitalizationType = .none")
                for (part in type.split('|')) {
                    when (part) {
                        "none" -> {

                        }
                        "text" -> {

                        }
                        "textCapCharacters" -> {
                            appendln("view.autocapitalizationType = .allCharacters")
                        }
                        "textCapWords" -> {
                            appendln("view.autocapitalizationType = .words")
                        }
                        "textCapSentences" -> {
                            appendln("view.autocapitalizationType = .sentences")
                        }
                        "textAutoCorrect" -> {
                            appendln("view.autocorrectionType = .yes")
                        }
                        "textAutoComplete" -> {
                            appendln("view.autocorrectionType = .yes")
                        }
                        "textMultiLine", "textImeMultiLine" -> {

                        }
                        "textNoSuggestions" -> {
                            appendln("view.spellCheckingType = .no")
                        }
                        "textUri" -> {
                            appendln("view.keyboardType = .URL")
                            appendln("view.textContentType = .URL")
                        }
                        "textEmailAddress", "textWebEmailAddress" -> {
                            appendln("view.textContentType = .emailAddress")
                            appendln("view.keyboardType = .emailAddress")
                        }
                        "textEmailSubject" -> {

                        }
                        "textShortMessage" -> {

                        }
                        "textLongMessage" -> {

                        }
                        "textPersonName" -> {
                            appendln("view.textContentType = .name")
                        }
                        "textPostalAddress" -> {
                            appendln("view.textContentType = .fullStreetAddress")
                        }
                        "textPassword", "textWebPassword" -> {
                            appendln("view.textContentType = .password")
                            appendln("view.isSecureTextEntry = true")
                        }
                        "textVisiblePassword" -> {
                            appendln("view.textContentType = .password")
                        }
                        "textWebEditText" -> {

                        }
                        "textFilter" -> {

                        }
                        "textPhonetic" -> {

                        }
                        "number" -> {
                            appendln("view.keyboardType = .numberPad")
                        }
                        "numberSigned" -> {
                            appendln("view.keyboardType = .numbersAndPunctuation")
                        }
                        "numberDecimal" -> {
                            appendln("view.keyboardType = .numbersAndPunctuation")
                        }
                        "numberPassword" -> {
                            appendln("view.keyboardType = .numberPad")
                            appendln("view.addDismissButton()")
                            appendln("view.textContentType = .password")
                            appendln("view.isSecureTextEntry = true")
                        }
                        "phone" -> {
                            appendln("view.keyboardType = .phonePad")
                            appendln("view.addDismissButton()")
                            appendln("view.textContentType = .telephoneNumber")
                        }
                        "datetime" -> {

                        }
                        "date" -> {

                        }
                        "time" -> {

                        }
                    }
                }
            }
            handleCommonText(node)
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
        ViewType("RadioButton", "UIButton", "Button") {},
        ViewType("ImageButton", "UIButton", "Button") { node -> },
        ViewType("Button", "UIButton", "View") { node ->
            node.attributeAsString("android:text")?.let { text ->
                appendln("view.setTitle($text, for: .normal)")
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
        },


        ViewType("LinearLayout", "UIView", "View") { node ->
            val isHorizontal = when (node.attributes["android:orientation"]) {
                "horizontal" -> true
                "vertical" -> false
                else -> true
            }
            val alignDimension = if (isHorizontal) "android:layout_height" else "android:layout_width"
            val alignWords = if (isHorizontal) verticalGravityWords else horizontalGravityWords
            val justifyWords = if (isHorizontal) horizontalGravityWords else verticalGravityWords

            val dividerStart = node.attributes["android:showDividers"]?.contains("beginning") ?: false
            val dividerMiddle = node.attributes["android:showDividers"]?.contains("middle") ?: false
            val dividerEnd = node.attributes["android:showDividers"]?.contains("end") ?: false

            append("view.flex.direction(")
            append(if (isHorizontal) ".row" else ".column")
            append(").padding(")
            val defaultPadding = node.attributeAsDimension("android:padding") ?: 0
            append((node.attributeAsDimension("android:paddingTop") ?: defaultPadding).toString())
            append(", ")
            append((node.attributeAsDimension("android:paddingLeft") ?: defaultPadding).toString())
            append(", ")
            append((node.attributeAsDimension("android:paddingBottom") ?: defaultPadding).toString())
            append(", ")
            append((node.attributeAsDimension("android:paddingRight") ?: defaultPadding).toString())
            append(").alignContent(")
            val defaultAlign = node.attributes["android:gravity"]
                ?.split('|')
                ?.asSequence()
                ?.mapNotNull { alignWords[it] }
                ?.firstOrNull()
                ?: ".start"
            append(defaultAlign)
            append(").justifyContent(")
            append(node.attributes["android:gravity"]
                ?.split('|')
                ?.asSequence()
                ?.mapNotNull { justifyWords[it] }
                ?.firstOrNull()
                ?: ".start"
            )
            appendln(").define{ (flex) in ")

            val dividerText = node.attributes["tools:iosDivider"] ?: "flex.addItem().height(1).backgroundColor(.gray)"

            if (dividerStart) {
                appendln(dividerText)
            }
            node.children.forEachBetween(
                forItem = { child ->
                    append("flex.addItem(")
                    write(child)
                    append(").margin(")
                    val defaultMargin = child.attributeAsDimension("android:layout_margin") ?: 0
                    append((child.attributeAsDimension("android:layout_marginTop") ?: defaultMargin).toString())
                    append(", ")
                    append((child.attributeAsDimension("android:layout_marginLeft") ?: defaultMargin).toString())
                    append(", ")
                    append((child.attributeAsDimension("android:layout_marginBottom") ?: defaultMargin).toString())
                    append(", ")
                    append((child.attributeAsDimension("android:layout_marginRight") ?: defaultMargin).toString())
                    append(")")
                    child.attributeAsDimension("android:layout_weight")?.let { weight ->
                        append(".grow($weight).shrink($weight)")
                    }
                    child.attributeAsDimension("android:layout_width")?.let { s ->
                        append(".width($s)")
                    }
                    child.attributeAsDimension("android:layout_height")?.let { s ->
                        append(".height($s)")
                    }
                    child.attributeAsDimension("android:minWidth")?.let { s ->
                        append(".minWidth($s)")
                    }
                    child.attributeAsDimension("android:minHeight")?.let { s ->
                        append(".minHeight($s)")
                    }
                    child.attributes[alignDimension]?.let {
                        if (it == "match_parent") {
                            append(".alignSelf(.stretch)")
                        } else {
                            null
                        }
                    } ?: child.attributes["android:layout_gravity"]
                        ?.split('|')
                        ?.asSequence()
                        ?.mapNotNull { alignWords[it] }
                        ?.firstOrNull()
                        ?.let { align ->
                            append(".alignSelf($align)")
                        } ?: append(".alignSelf($defaultAlign)")
                    appendln()
                    appendln()
                },
                between = {
                    if (dividerMiddle) {
                        appendln(dividerText)
                    }
                }
            )
            if (dividerEnd) {
                appendln(dividerText)
            }

            appendln("}")
        },

        ViewType("FrameLayout", "UIView", "View") { node ->
            val defaultPadding = node.attributeAsDimension("android:padding") ?: 0
            val paddingTop = (node.attributeAsDimension("android:paddingTop") ?: defaultPadding).toString()
            val paddingLeft = (node.attributeAsDimension("android:paddingLeft") ?: defaultPadding).toString()
            val paddingBottom = (node.attributeAsDimension("android:paddingBottom") ?: defaultPadding).toString()
            val paddingRight = (node.attributeAsDimension("android:paddingRight") ?: defaultPadding).toString()

            for (child in node.children) {
                appendln("view.addSubview({")
                append("let sub = ")
                write(child)
                appendln()

                val defaultMargin = child.attributeAsDimension("android:layout_margin") ?: 0
                val marginTop = (child.attributeAsDimension("android:layout_marginTop") ?: defaultMargin).toString()
                val marginLeft = (child.attributeAsDimension("android:layout_marginLeft") ?: defaultMargin).toString()
                val marginBottom = (child.attributeAsDimension("android:layout_marginBottom") ?: defaultMargin).toString()
                val marginRight = (child.attributeAsDimension("android:layout_marginRight") ?: defaultMargin).toString()

                appendln("view.addOnLayoutSubviews { [weak view, weak sub] in")
                appendln("if let view = view, let sub = sub {")
                append("sub.pin")
                when (child.attributes["android:layout_width"]) {
                    "wrap_content", null -> append(".width(sub.intrinsicContentSize.width)")
                    "match_parent" -> append(".width(100%)")
                    else -> child.attributeAsDimension("android:layout_width")?.let { s ->
                        append(".width($s)")
                    } ?: append(".width(sub.intrinsicContentSize.width)")
                }
                when (child.attributes["android:layout_height"]) {
                    "wrap_content", null -> append(".height(sub.intrinsicContentSize.height)")
                    "match_parent" -> append(".height(100%)")
                    else -> child.attributeAsDimension("android:layout_height")?.let { s ->
                        append(".height($s)")
                    } ?: append(".height(sub.intrinsicContentSize.height)")
                }
                child.attributes["android:layout_gravity"]?.let {
                    for (part in it.split('|')) {
                        when (part) {
                            "left" -> append(".left($marginLeft + $paddingLeft)")
                            "right" -> append(".right($marginRight + $paddingRight)")
                            "start" -> append(".start($marginLeft + $paddingLeft)")
                            "end" -> append(".end($marginRight + $paddingRight)")
                            "center_horizontal" -> append(".hCenter()")
                            "top" -> append(".top($marginTop + $paddingTop)")
                            "bottom" -> append(".bottom($marginBottom + $paddingBottom)")
                            "center_vertical" -> append(".vCenter()")
                            "center" -> append(".center()")
                        }
                    }
                }
                appendln()
                appendln("}")
                appendln("}")

                appendln("return sub")

                appendln("}())")
                appendln()
            }
        }
    ),
    skipTypes = listOf(
        "android.support.v4.widget.SwipeRefreshLayout",
        "SwipeRefreshLayout"
    )
)


private fun Appendable.handleCommonText(node: XmlNode, viewHandle: String = "view") {
    node.attributeAsString("android:text")?.let { text ->
        appendln("$viewHandle.text = $text")
    }
    node.attributeAsDouble("android:lineSpacingMultiplier")?.let { lineSpacingMultiplier ->
        appendln("$viewHandle.lineSpacingMultiplier = $lineSpacingMultiplier")
    }
    val lines = node.attributeAsInt("android:maxLines")
    appendln("$viewHandle.numberOfLines = ${lines ?: 0}")
    val size = node.attributeAsDimension("android:textSize") ?: "12"
    val fontStyles = node.attributes["android:textStyle"]?.split('|') ?: listOf()
    appendln("$viewHandle.font = UIFont.get(size: $size, style: [${fontStyles.joinToString { "\"$it\"" }}])")
    node.attributeAsColor("android:textColor")?.let {
        appendln("$viewHandle.textColor = $it")
    }
    node.attributes["android:gravity"]?.let {
        it.split('|')
            .asSequence()
            .mapNotNull { horizontalGravityWords[it] }
            .firstOrNull()
            ?.let {
                val fixed = when (it) {
                    ".start" -> ".left"
                    ".center" -> ".center"
                    ".end" -> ".right"
                    else -> ".left"
                }
                appendln("$viewHandle.textAlignment = $fixed")
            }
        it.split('|')
            .asSequence()
            .mapNotNull { verticalGravityWords[it] }
            .firstOrNull()
            ?.let {
                val fixed = when (it) {
                    ".start" -> ".none"
                    ".center" -> ".alignCenters"
                    ".end" -> ".alignBaselines"
                    else -> ".none"
                }
                appendln("$viewHandle.baselineAdjustment = $fixed")
            }
    }
}

private val horizontalGravityWords = mapOf(
    "left" to ".start",
    "right" to ".end",
    "center_horizontal" to ".center",
    "start" to ".start",
    "end" to ".end",
    "center" to ".center"
)
private val verticalGravityWords = mapOf(
    "top" to ".start",
    "bottom" to ".end",
    "center_vertical" to ".center",
    "center" to ".center"
)
