package com.lightningkite.khrysalis.ios.layout

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.ios.*

val LayoutConverter.Companion.textInputViews
    get() = LayoutConverter(
        viewTypes = ViewType.mapOf(
            ViewType("com.lightningkite.butterfly.views.widget.MultilineEditText", "UITextView", "EditText", handlesPadding = true) { node ->
                //Purposefully empty
                node.attributeAsSwiftString("android:hint")?.let { text ->
                    appendLine("view.placeholder = $text")
                }
                node.attributeAsSwiftColor("android:textColorHint")?.let {
                    appendLine("view.placeholderColor = $it")
                }
                appendLine("view.addDismissButton()")
            },
            ViewType("AutoCompleteTextView", "UIAutoCompleteTextFieldPadded", "EditText", handlesPadding = true) { node ->},
            ViewType("EditText", "UITextFieldPadded", "View", handlesPadding = true) { node ->
                val defaultPadding = node.attributeAsSwiftDimension("android:padding") ?: 0
                val paddingTop = (node.attributeAsSwiftDimension("android:paddingTop") ?: defaultPadding)
                val paddingLeft = (node.attributeAsSwiftDimension("android:paddingLeft") ?: defaultPadding)
                val paddingBottom = (node.attributeAsSwiftDimension("android:paddingBottom") ?: defaultPadding)
                val paddingRight = (node.attributeAsSwiftDimension("android:paddingRight") ?: defaultPadding)
                appendLine("view.padding = UIEdgeInsets(top: $paddingTop, left: $paddingLeft, bottom: $paddingBottom, right: $paddingRight)")

                node.attributeAsSwiftDrawable("android:drawableLeft")?.let {
                    appendLine("view.leftView = $it.makeView()")
                    appendLine("view.leftViewMode = .always")
                }
                node.attributeAsSwiftDrawable("android:drawableRight")?.let {
                    appendLine("view.rightView = $it.makeView()")
                    appendLine("view.rightViewMode = .always")
                }
                node.attributeAsSwiftDimension("android:compoundPadding")?.let {
                    appendLine("view.compoundPadding = $it")
                }

                node.attributeAsSwiftString("android:hint")?.let { text ->
                    if (!setToColor(node, "android:textColorHint") { it, s ->
                            appendLine("view.attributedPlaceholder = $text.attributedWithColor($it)")
                        }) {
                        appendLine("view.placeholder = $text")
                    }
                }
                if (node.allAttributes["android:background"] == null) {
                    val boldColor = node.attributeAsSwiftColor("android:textColor") ?: "UIColor.white"
                    val hintColor = node.attributeAsSwiftColor("android:textColorHint") ?: "nil"
                    appendLine("view.setBackgroundColor(.clear)")
                    appendLine("view.backgroundLayer = view.underlineLayer(boldColor: $boldColor, hintColor: $hintColor)")
                }
                node.allAttributes["android:imeOptions"]?.split("|")?.forEach {
                    appendLine(
                        "view.returnKeyType = .${
                            when (it) {
                                "actionSend" -> "send"
                                "actionDone" -> "done"
                                "actionGo" -> "go"
                                "actionNext" -> "next"
                                "actionSearch" -> "search"
                                else -> "default"
                            }
                        }"
                    )
                }
                node.allAttributes["android:inputType"]?.let { type ->
                    appendLine("view.autocapitalizationType = .none")
                    for (part in type.split('|')) {
                        when (part) {
                            "none" -> {

                            }
                            "text" -> {

                            }
                            "textCapCharacters" -> {
                                appendLine("view.autocapitalizationType = .allCharacters")
                            }
                            "textCapWords" -> {
                                appendLine("view.autocapitalizationType = .words")
                            }
                            "textCapSentences" -> {
                                appendLine("view.autocapitalizationType = .sentences")
                            }
                            "textAutoCorrect" -> {
                                appendLine("view.autocorrectionType = .yes")
                            }
                            "textAutoComplete" -> {
                                appendLine("view.autocorrectionType = .yes")
                            }
                            "textMultiLine", "textImeMultiLine" -> {

                            }
                            "textNoSuggestions" -> {
                                appendLine("view.spellCheckingType = .no")
                            }
                            "textUri" -> {
                                appendLine("view.keyboardType = .URL")
                                appendLine("view.textContentType = .URL")
                            }
                            "textEmailAddress", "textWebEmailAddress" -> {
                                appendLine("view.textContentType = .emailAddress")
                                appendLine("view.keyboardType = .emailAddress")
                            }
                            "textEmailSubject" -> {

                            }
                            "textShortMessage" -> {

                            }
                            "textLongMessage" -> {

                            }
                            "textPersonName" -> {
                                appendLine("view.textContentType = .name")
                            }
                            "textPostalAddress" -> {
                                appendLine("view.textContentType = .fullStreetAddress")
                            }
                            "textPassword", "textWebPassword" -> {
                                appendLine("if #available(iOS 11.0, *) { view.textContentType = .password }")
                                appendLine("view.isSecureTextEntry = true")
                            }
                            "textVisiblePassword" -> {
                                appendLine("if #available(iOS 11.0, *) { view.textContentType = .password }")
                            }
                            "textWebEditText" -> {

                            }
                            "textFilter" -> {

                            }
                            "textPhonetic" -> {

                            }
                            "number" -> {
                                appendLine("view.keyboardType = .numberPad")
                                appendLine("view.addDismissButton()")
                            }
                            "numberSigned" -> {
                                appendLine("view.keyboardType = .numbersAndPunctuation")
                                appendLine("view.addDismissButton()")
                            }
                            "numberDecimal" -> {
                                appendLine("view.keyboardType = .decimalPad")
                                appendLine("view.addDismissButton()")
                            }
                            "numberPassword" -> {
                                appendLine("view.keyboardType = .numberPad")
                                appendLine("view.addDismissButton()")
                                appendLine("if #available(iOS 11.0, *) { view.textContentType = .password }")
                                appendLine("view.isSecureTextEntry = true")
                            }
                            "phone" -> {
                                appendLine("view.keyboardType = .phonePad")
                                appendLine("view.addDismissButton()")
                                appendLine("view.textContentType = .telephoneNumber")
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
                node.allAttributes["android:autofillHints"]?.let { type ->
                    when (type) {
                        "creditCardNumber" -> appendLine("view.textContentType = .creditCardNumber")
                        "emailAddress" -> appendLine("view.textContentType = .emailAddress")
                        "name" -> appendLine("view.textContentType = .name")
//                        "newPassword" -> appendln("view.textContentType = .newPassword")
//                        "newUsername" -> appendln("view.textContentType = .username")
                        "password" -> appendLine("view.textContentType = .password")
                        "personName" -> appendLine("view.textContentType = .name")
                        "personFamilyName" -> appendLine("view.textContentType = .familyName")
                        "personGivenName" -> appendLine("view.textContentType = .givenName")
                        "personMiddleName" -> appendLine("view.textContentType = .middleName")
                        "personMiddleInitial" -> appendLine("view.textContentType = .middleName")
                        "personNamePrefix" -> appendLine("view.textContentType = .namePrefix")
                        "personNameSuffix" -> appendLine("view.textContentType = .nameSuffix")
                        "phone" -> appendLine("view.textContentType = .telephoneNumber")
                        "phoneNumber" -> appendLine("view.textContentType = .telephoneNumber")
                        "phoneNumberDevice" -> appendLine("view.textContentType = .telephoneNumber")
                        "postalAddress" -> appendLine("view.textContentType = .streetAddressLine1")
                        "addressCountry" -> appendLine("view.textContentType = .countryName")
                        "extendedAddress" -> appendLine("view.textContentType = .streetAddressLine2")
                        "extendedPostalCode" -> appendLine("view.textContentType = .extendedPostalCode")
                        "addressLocality" -> appendLine("view.textContentType = .addressCity")
                        "addressRegion" -> appendLine("view.textContentType = .addressState")
                        "streetAddress" -> appendLine("view.textContentType = .streetAddressLine1")
                        "postalCode" -> appendLine("view.textContentType = .postalCode")
                        "username" -> appendLine("view.textContentType = .username")
                        else -> {
                        }
                    }
                }
                handleCommonText(node, controlView = "view")
            }
        )
    )
