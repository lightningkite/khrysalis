package com.lightningkite.khrysalis.ios.layout

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.ios.*

val LayoutConverter.Companion.textInputViews
    get() = LayoutConverter(
        viewTypes = ViewType.mapOf(
            ViewType("com.lightningkite.butterfly.views.widget.MultilineEditText", "UITextView", "EditText", handlesPadding = true) { node ->
                //Purposefully empty
                node.attributeAsSwiftString("android:hint")?.let { text ->
                    appendln("view.placeholder = $text")
                }
                node.attributeAsSwiftColor("android:textColorHint")?.let {
                    appendln("view.placeholderColor = $it")
                }
                appendln("view.addDismissButton()")
            },
            ViewType("AutoCompleteTextView", "UIAutoCompleteTextFieldPadded", "EditText", handlesPadding = true) { node ->},
            ViewType("EditText", "UITextFieldPadded", "View", handlesPadding = true) { node ->
                val defaultPadding = node.attributeAsSwiftDimension("android:padding") ?: 0
                val paddingTop = (node.attributeAsSwiftDimension("android:paddingTop") ?: defaultPadding)
                val paddingLeft = (node.attributeAsSwiftDimension("android:paddingLeft") ?: defaultPadding)
                val paddingBottom = (node.attributeAsSwiftDimension("android:paddingBottom") ?: defaultPadding)
                val paddingRight = (node.attributeAsSwiftDimension("android:paddingRight") ?: defaultPadding)
                appendln("view.padding = UIEdgeInsets(top: $paddingTop, left: $paddingLeft, bottom: $paddingBottom, right: $paddingRight)")

                node.attributeAsSwiftDrawable("android:drawableLeft")?.let {
                    appendln("view.leftView = $it.makeView()")
                    appendln("view.leftViewMode = .always")
                }
                node.attributeAsSwiftDrawable("android:drawableRight")?.let {
                    appendln("view.rightView = $it.makeView()")
                    appendln("view.rightViewMode = .always")
                }
                node.attributeAsSwiftDimension("android:compoundPadding")?.let {
                    appendln("view.compoundPadding = $it")
                }

                node.attributeAsSwiftString("android:hint")?.let { text ->
                    if(!setToColor(node, "android:textColorHint"){ it, s ->
                            appendln("view.attributedPlaceholder = $text.attributedWithColor($it)")
                    }) {
                        appendln("view.placeholder = $text")
                    }
                }
                if (node.allAttributes["android:background"] == null) {
                    val boldColor = node.attributeAsSwiftColor("android:textColor") ?: "UIColor.white"
                    val hintColor = node.attributeAsSwiftColor("android:textColorHint") ?: "nil"
                    appendln("view.backgroundLayer = view.underlineLayer(boldColor: $boldColor, hintColor: $hintColor)")
                }
                node.allAttributes["android:imeOptions"]?.split("|")?.forEach {
                    appendln("view.returnKeyType = .${when(it){
                        "actionSend" -> "send"
                        "actionDone" -> "done"
                        "actionGo" -> "go"
                        "actionNext" -> "next"
                        "actionSearch" -> "search"
                        else -> "default"
                    }}")
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
                                appendln("if #available(iOS 11.0, *) { view.textContentType = .password }")
                                appendln("view.isSecureTextEntry = true")
                            }
                            "textVisiblePassword" -> {
                                appendln("if #available(iOS 11.0, *) { view.textContentType = .password }")
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
                                appendln("view.keyboardType = .decimalPad")
                                appendln("view.addDismissButton()")
                            }
                            "numberPassword" -> {
                                appendln("view.keyboardType = .numberPad")
                                appendln("view.addDismissButton()")
                                appendln("if #available(iOS 11.0, *) { view.textContentType = .password }")
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
                node.allAttributes["android:autofillHints"]?.let { type ->
                    when(type){
                        "creditCardNumber" -> appendln("view.textContentType = .creditCardNumber")
                        "emailAddress" -> appendln("view.textContentType = .emailAddress")
                        "name" -> appendln("view.textContentType = .name")
//                        "newPassword" -> appendln("view.textContentType = .newPassword")
//                        "newUsername" -> appendln("view.textContentType = .username")
                        "password" -> appendln("view.textContentType = .password")
                        "personName" -> appendln("view.textContentType = .name")
                        "personFamilyName" -> appendln("view.textContentType = .familyName")
                        "personGivenName" -> appendln("view.textContentType = .givenName")
                        "personMiddleName" -> appendln("view.textContentType = .middleName")
                        "personMiddleInitial" -> appendln("view.textContentType = .middleName")
                        "personNamePrefix" -> appendln("view.textContentType = .namePrefix")
                        "personNameSuffix" -> appendln("view.textContentType = .nameSuffix")
                        "phone" -> appendln("view.textContentType = .telephoneNumber")
                        "phoneNumber" -> appendln("view.textContentType = .telephoneNumber")
                        "phoneNumberDevice" -> appendln("view.textContentType = .telephoneNumber")
                        "postalAddress" -> appendln("view.textContentType = .streetAddressLine1")
                        "addressCountry" -> appendln("view.textContentType = .countryName")
                        "extendedAddress" -> appendln("view.textContentType = .streetAddressLine2")
                        "extendedPostalCode" -> appendln("view.textContentType = .extendedPostalCode")
                        "addressLocality" -> appendln("view.textContentType = .addressCity")
                        "addressRegion" -> appendln("view.textContentType = .addressState")
                        "streetAddress" -> appendln("view.textContentType = .streetAddressLine1")
                        "postalCode" -> appendln("view.textContentType = .postalCode")
                        "username" -> appendln("view.textContentType = .username")
                        else -> {}
                    }
                }
                handleCommonText(node, controlView = "view")
            }
        )
    )
