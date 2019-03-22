package com.lightningkite.kwift.layoutxml

import com.lightningkite.kwift.utils.camelCase

fun ViewType.Companion.setupNormalViewTypes() {


    register("View", "UIView") { node ->
        node.attributes["android:id"]?.let { raw ->
            val name = "boundView" + raw.removePrefix("@+id/").camelCase().capitalize()
            appendln("self.$name = view")
            bindings[name] = ViewType.registry[node.name]?.iosName ?: "UIView"
        }
        node.attributes["android:background"]?.let { raw ->
            when {
                raw.startsWith("@drawable/") -> {
                    val drawableName = raw.removePrefix("@drawable/")
                    appendln("if let image = UIImage(named: \"$drawableName\") {")
                    appendln("view.backgroundColor = UIColor(patternImage: image)")
                    appendln("} else {")
                    appendln("ResourcesBackground.apply(self, view, \"$drawableName\")")
                    appendln("}")
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
    }


    register("RadioGroup", "UIView", "LinearLayout"){}
    register("RadioButton", "UIButton", "ImageButton"){}
    register("ImageButton", "UIButton", "Button"){ node -> }


    register("ImageView", "UIImageView", "View") { node ->
        node.attributeAsImage("android:src")?.let { text ->
            appendln("view.image = $text")
        }
        appendln(
            "view.contentMode = ${when (node.attributes["android:scaleType"]) {
                "fitXY" -> ".scaleToFill"
                "centerCrop" -> ".scaleAspectFill"
                "centerInside" -> ".scaleAspectFit"
                else -> ".scaleAspectFit"
            }}"
        )
    }


    register("TextView", "UILabel", "View") { node ->
        val lines = node.attributeAsInt("android:maxLines")
        appendln("view.numberOfLines = ${lines ?: 0}")
        handleCommonText(node)
    }


    register("ScrollView", "UIScrollView", "View") { node ->
        appendln("view.flex.direction(.column).alignContent(.center).addItem(")
        ViewType.write(this, node.children.first())
        if (node.attributes["android:fillViewport"] == "true") {
            appendln(").grow(1)")
        } else {
            appendln(")")
        }
    }


    register("EditText", "UITextField", "View") { node ->
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
                        appendln("view.textContentType = .password")
                        appendln("view.isSecureTextEntry = true")
                    }
                    "phone" -> {
                        appendln("view.keyboardType = .phonePad")
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
    }


    register("Button", "UIButton", "View") { node ->
        node.attributeAsString("android:text")?.let { text ->
            appendln("view.setTitle($text, for: .normal)")
        }
        node.attributeAsColor("android:textColor")?.let {
            appendln("view.setTitleColor($it, for: .normal)")
        }
        val size = node.attributeAsDimension("android:textSize") ?: "12"
        val fontStyles = node.attributes["android:textStyle"]?.split('|') ?: listOf()
        appendln("view.titleLabel?.font = UIFont.get(size: $size, style: [${fontStyles.joinToString { "\"$it\"" }}])")
        val lines = node.attributeAsInt("android:maxLines")
        appendln("view.titleLabel?.numberOfLines = ${lines ?: 0}")
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
                appendln("view.titleLabel?.textAlignment = $fixed")
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
    }



    register("LinearLayout", "UIView", "View") { node ->
        val isHorizontal = when (node.attributes["android:orientation"]) {
            "horizontal" -> true
            "vertical" -> false
            else -> true
        }
        val alignDimension = if(isHorizontal) "android:layout_height" else "android:layout_width"
        val alignWords = if (isHorizontal) verticalGravityWords else horizontalGravityWords
        val justifyWords = if (isHorizontal) horizontalGravityWords else verticalGravityWords

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
        for (child in node.children) {
            append("flex.addItem(")
            ViewType.write(this, child)
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
                if(it == "match_parent"){
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
        }
        appendln("}")
    }

    register("FrameLayout", "UIView", "View") { node ->
        append("view.flex")
        append(".padding(")
        val defaultPadding = node.attributeAsDimension("android:padding") ?: 0
        append((node.attributeAsDimension("android:paddingTop") ?: defaultPadding).toString())
        append(", ")
        append((node.attributeAsDimension("android:paddingLeft") ?: defaultPadding).toString())
        append(", ")
        append((node.attributeAsDimension("android:paddingBottom") ?: defaultPadding).toString())
        append(", ")
        append((node.attributeAsDimension("android:paddingRight") ?: defaultPadding).toString())
        appendln(").define{ (flex) in ")

        for (child in node.children) {
            append("flex.addItem(")
            ViewType.write(this, child)
            append(").position(.absolute).margin(")
            val defaultMargin = child.attributeAsDimension("android:layout_margin") ?: 0
            append((child.attributeAsDimension("android:layout_marginTop") ?: defaultMargin).toString())
            append(", ")
            append((child.attributeAsDimension("android:layout_marginLeft") ?: defaultMargin).toString())
            append(", ")
            append((child.attributeAsDimension("android:layout_marginBottom") ?: defaultMargin).toString())
            append(", ")
            append((child.attributeAsDimension("android:layout_marginRight") ?: defaultMargin).toString())
            append(")")

            child.attributeAsDimension("android:layout_width")?.let { s ->
                append(".width($s)")
            }
            child.attributeAsDimension("android:layout_height")?.let { s ->
                append(".height($s)")
            }
            child.attributes["android:layout_width"]?.let {
                if(it == "match_parent")
                    append(".width(100%)")
            }
            child.attributes["android:layout_height"]?.let {
                if(it == "match_parent")
                    append(".height(100%)")
            }
            child.attributes["android:layout_gravity"]?.let {
                for(part in it.split('|')) {
                    when(part){
                        "left" -> append(".left(0)")
                        "right" -> append(".right(0)")
                        "start" -> append(".start(0)")
                        "end" -> append(".end(0)")
                        "center_horizontal" -> append(".start(0).end(0)")
                        "top" -> append(".top(0)")
                        "bottom" -> append(".bottom(0)")
                        "center_vertical" -> append(".top(0).bottom(0)")
                        "center" -> append(".top(0).bottom(0).start(0).end(0)")
                    }
                }
            }
            appendln()
        }

        appendln("}")
    }
}

private fun Appendable.handleCommonText(node: XmlNode) {
    node.attributeAsString("android:text")?.let { text ->
        appendln("view.text = $text")
    }
    val size = node.attributeAsDimension("android:textSize") ?: "12"
    val fontStyles = node.attributes["android:textStyle"]?.split('|') ?: listOf()
    appendln("view.font = UIFont.get(size: $size, style: [${fontStyles.joinToString { "\"$it\"" }}])")
    node.attributeAsColor("android:textColor")?.let {
        appendln("view.textColor = $it")
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
                appendln("view.textAlignment = $fixed")
            }
    }
}

val horizontalGravityWords = mapOf(
    "left" to ".start",
    "right" to ".end",
    "center_horizontal" to ".center",
    "start" to ".start",
    "end" to ".end",
    "center" to ".center"
)
val verticalGravityWords = mapOf(
    "top" to ".start",
    "bottom" to ".end",
    "center_vertical" to ".center",
    "center" to ".center"
)
