package com.lightningkite.kwift.layoutxml

import com.lightningkite.kwift.utils.camelCase
import com.lightningkite.kwift.utils.forEachBetween

fun ViewType.Companion.setupNormalViewTypes() {


    register("View", "UIView") { node ->
        node.attributes["android:id"]?.let { raw ->
            val name = "boundView" + raw.removePrefix("@+id/").removePrefix("@id/").camelCase().capitalize()
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
        node.attributeAsDimension("android:elevation")?.let {
            appendln("view.layer.masksToBounds = false")
            appendln("view.layer.shadowColor = UIColor.black.cgColor")
            appendln("view.layer.shadowOffset = CGSize(width: 0, height: $it)")
            appendln("view.layer.shadowRadius = $it")
            appendln("view.layer.shadowOpacity = 0.24")
        }
    }


    register("android.support.v7.widget.RecyclerView", "UITableView", "View"){}
    register("Space", "UIView", "View"){}
    skipTypes += "android.support.v4.widget.SwipeRefreshLayout"
    skipTypes += "SwipeRefreshLayout"


    register("ImageView", "UIImageView", "View") { node ->
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
    }
    register("de.hdodenhof.circleimageview.CircleImageView", "UIImageView", "ImageView") { node ->

        appendln("self.onLayoutSubviews.addWeak(view){ view, _ in")
        appendln("    view.layer.cornerRadius = view.frame.size.width / 2;")
        appendln("}")
        appendln("view.clipsToBounds = true")
    }


    register("TextView", "UILabel", "View") { node ->
        val lines = node.attributeAsInt("android:maxLines")
        appendln("view.numberOfLines = ${lines ?: 0}")
        handleCommonText(node)
    }


    register("ScrollView", "UIScrollView", "View") { node ->
//        val child = node.children.first()
//
//        appendln("view.addSubview({")
//        append("let sub = ")
//        ViewType.write(this, child)
//        appendln()
//
//        val defaultMargin = node.attributeAsDimension("android:layout_margin") ?: 0
//        val marginTop = (node.attributeAsDimension("android:layout_marginTop") ?: defaultMargin).toString()
//        val marginLeft = (node.attributeAsDimension("android:layout_marginLeft") ?: defaultMargin).toString()
//        val marginBottom = (node.attributeAsDimension("android:layout_marginBottom") ?: defaultMargin).toString()
//        val marginRight = (node.attributeAsDimension("android:layout_marginRight") ?: defaultMargin).toString()
//
//        appendln("onLayoutSubviews.addWeak(sub) { (sub: UIView, _: Void) in")
//        append("sub.pin.width(100%).top(0)")
//        if(child.attributes.containsKey("android:fillViewport")){
//            append(".minHeight(100%)")
//        }
//        appendln()
//        appendln("view.contentSize = sub.frame.size")
//        appendln("}")
//
//        appendln("return sub")
//
//        appendln("}())")
        val child = node.children.first()
        appendln("view.flex.direction(.column).alignContent(.center).addItem({")
        append("let sub = ")
        ViewType.write(this, child)
        appendln()
        appendln("self.onLayoutSubviews.addWeak(view, sub){ view, sub, _ in")
        appendln("    view.contentSize.height = sub.flex.intrinsicSize.height")
        appendln("}")
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
    }


    register("com.lightningkite.kwift.android.MultilineEditText", "UITextView", "EditText") { node ->

    }
    register("EditText", "UITextField", "View") { node ->
        val defaultPadding = node.attributeAsDimension("android:padding") ?: 0
        val paddingTop = (node.attributeAsDimension("android:paddingTop") ?: defaultPadding)
        val paddingLeft = (node.attributeAsDimension("android:paddingLeft") ?: defaultPadding)
        val paddingBottom = (node.attributeAsDimension("android:paddingBottom") ?: defaultPadding)
        val paddingRight = (node.attributeAsDimension("android:paddingRight") ?: defaultPadding)

        if(paddingLeft != 0){
            appendln("view.setLeftPaddingPoints($paddingLeft)")
        }
        if(paddingRight != 0){
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
        if(node.attributes["android:background"] == null){
            appendln("ResourcesBackground.apply(self, view, \"edit_text_background\")")
        }
        handleCommonText(node)
    }


    register("CheckBox", "LabeledSwitch", "View"){
        handleCommonText(it, "view.labelView")
    }
    register("Spinner", "Dropdown", "View"){}
    register("RadioGroup", "UIView", "LinearLayout"){}
    register("RadioButton", "UIButton", "Button"){}
    register("ImageButton", "UIButton", "Button"){ node -> }
    register("Button", "UIButton", "View") { node ->
        node.attributeAsString("android:text")?.let { text ->
            appendln("view.setTitle($text, for: .normal)")
        }
        node.attributeAsColor("android:textColor")?.let {
            appendln("view.setTitleColor($it, for: .normal)")
        }
        val size = node.attributeAsDimension("android:textSize") ?: "12"
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
            appendln("onLayoutSubviews.addWeak(view) { (view: UIButton, _: Void) in")
            appendln("    view.setTitlePosition(.bottom)")
            appendln("}")
        }
        node.attributeAsImage("android:drawableRight")?.let { text ->
            appendln("view.setImage($text, for: .normal)")
            appendln("onLayoutSubviews.addWeak(view) { (view: UIButton, _: Void) in")
            appendln("    view.setTitlePosition(.left)")
            appendln("}")
        }
        node.attributeAsImage("android:drawableBottom")?.let { text ->
            appendln("view.setImage($text, for: .normal)")
            appendln("onLayoutSubviews.addWeak(view) { (view: UIButton, _: Void) in")
            appendln("    view.setTitlePosition(.top)")
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



    register("LinearLayout", "UIView", "View") { node ->
        val isHorizontal = when (node.attributes["android:orientation"]) {
            "horizontal" -> true
            "vertical" -> false
            else -> true
        }
        val alignDimension = if(isHorizontal) "android:layout_height" else "android:layout_width"
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

        if(dividerStart){
            appendln(dividerText)
        }
        node.children.forEachBetween(
            forItem = { child ->
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
                appendln()
            },
            between = {
                if(dividerMiddle){
                    appendln(dividerText)
                }
            }
        )
        if(dividerEnd){
            appendln(dividerText)
        }

        appendln("}")
    }

    register("FrameLayout", "UIView", "View") { node ->
        val defaultPadding = node.attributeAsDimension("android:padding") ?: 0
        val paddingTop = (node.attributeAsDimension("android:paddingTop") ?: defaultPadding).toString()
        val paddingLeft = (node.attributeAsDimension("android:paddingLeft") ?: defaultPadding).toString()
        val paddingBottom = (node.attributeAsDimension("android:paddingBottom") ?: defaultPadding).toString()
        val paddingRight = (node.attributeAsDimension("android:paddingRight") ?: defaultPadding).toString()

        for (child in node.children) {
            appendln("view.addSubview({")
            append("let sub = ")
            ViewType.write(this, child)
            appendln()

            val defaultMargin = node.attributeAsDimension("android:layout_margin") ?: 0
            val marginTop = (node.attributeAsDimension("android:layout_marginTop") ?: defaultMargin).toString()
            val marginLeft = (node.attributeAsDimension("android:layout_marginLeft") ?: defaultMargin).toString()
            val marginBottom = (node.attributeAsDimension("android:layout_marginBottom") ?: defaultMargin).toString()
            val marginRight = (node.attributeAsDimension("android:layout_marginRight") ?: defaultMargin).toString()

            appendln("onLayoutSubviews.addWeak(sub) { (sub: UIView, _: Void) in")
            append("sub.pin")
            when(child.attributes["android:layout_width"]) {
                "wrap_content", null -> append(".width(sub.intrinsicContentSize.width)")
                "match_parent" -> append(".width(100%)")
                else -> child.attributeAsDimension("android:layout_width")?.let { s ->
                    append(".width($s)")
                } ?: append(".width(sub.intrinsicContentSize.width)")
            }
            when(child.attributes["android:layout_height"]) {
                "wrap_content", null -> append(".height(sub.intrinsicContentSize.height)")
                "match_parent" -> append(".height(100%)")
                else -> child.attributeAsDimension("android:layout_height")?.let { s ->
                    append(".height($s)")
                } ?: append(".height(sub.intrinsicContentSize.height)")
            }
            child.attributes["android:layout_gravity"]?.let {
                for(part in it.split('|')) {
                    when(part){
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

            appendln("return sub")

            appendln("}())")
            appendln()
        }
    }
}

private fun Appendable.handleCommonText(node: XmlNode, viewHandle: String = "view") {
    node.attributeAsString("android:text")?.let { text ->
        appendln("$viewHandle.text = $text")
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
