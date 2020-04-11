package com.lightningkite.khrysalis.ios.layout

import com.lightningkite.khrysalis.utils.*

val LayoutConverter.Companion.textInputViews
    get() = LayoutConverter(
        viewTypes = ViewType.mapOf(
            ViewType("com.lightningkite.khrysalis.views.android.MultilineEditText", "UITextView", "EditText", handlesPadding = true) { node ->
                //Purposefully empty
                appendln("view.addDismissButton()")
            },
            ViewType("AutoCompleteTextView", "UIAutoCompleteTextField", "EditText", handlesPadding = true) { node ->},
            ViewType("EditText", "UITextField", "View", handlesPadding = true) { node ->
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
                    node.attributeAsColor("android:textColorHint")?.let { color ->
                        appendln("view.attributedPlaceholder = $text.attributedWithColor($color)")
                    } ?: run {
                        appendln("view.placeholder = $text")
                    }
                }
                if (node.allAttributes["android:background"] == null) {
                    val boldColor = node.attributeAsColor("android:textColor") ?: "UIColor.white"
                    val hintColor = node.attributeAsColor("android:textColorHint") ?: "nil"
                    appendln("view.backgroundLayer = view.underlineLayer(boldColor: $boldColor, hintColor: $hintColor)")
                }
                node.allAttributes["android:inputType"]?.let { type ->
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
                                appendln("view.addDismissButton()")
                            }
                            "numberSigned" -> {
                                appendln("view.keyboardType = .numbersAndPunctuation")
                                appendln("view.addDismissButton()")
                            }
                            "numberDecimal" -> {
                                appendln("view.keyboardType = .numbersAndPunctuation")
                                appendln("view.addDismissButton()")
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
                handleCommonText(node, controlView = "view")
            }
        )
    )