package com.lightningkite.khrysalis.web.layout

import com.lightningkite.khrysalis.utils.AlignDirection
import com.lightningkite.khrysalis.utils.horizontalGravity
import com.lightningkite.khrysalis.utils.verticalGravity
import com.lightningkite.khrysalis.web.asCssColor
import java.util.concurrent.atomic.AtomicInteger

val idNumber = AtomicInteger(0)

internal fun HtmlTranslator.input() {
    element.handle("EditText"){
        out.name = "div"
        out.classes += "khrc"
        val primary = ResultNode("input").apply{
            attributes["type"] = "text"
            style["font-size"] = "12pt"
        }
        out.contentNodes.add(primary)
        out.primary = primary
        out.text = primary
    }

    element.handle("com.lightningkite.butterfly.views.widget.MultilineEditText"){
        out.name = "div"
        out.classes += "khrc"
        val primary = ResultNode("textarea").apply{
            attributes["type"] = "text"
            style["font-size"] = "12pt"
        }
        out.contentNodes.add(primary)
        out.primary = primary
        out.text = primary
    }

    element.handle("com.lightningkite.butterfly.views.widget.DateButton"){
        out.name = "div"
        out.classes += "khrc"
        val primary = ResultNode("input").apply{
            attributes["type"] = "date"
            style["font-size"] = "12pt"
        }
        out.contentNodes.add(primary)
        out.primary = primary
        out.text = primary
    }

    element.handle("com.lightningkite.butterfly.views.widget.TimeButton"){
        out.name = "div"
        out.classes += "khrc"
        val primary = ResultNode("input").apply{
            attributes["type"] = "time"
            style["font-size"] = "12pt"
        }
        out.contentNodes.add(primary)
        out.primary = primary
        out.text = primary
    }

    element.handle("SeekBar"){
        out.name = "input"
        out.attributes["type"] = "range"
        out.attributes["min"] = "1"
        out.attributes["max"] = "100"
        out.attributes["value"] = "50"
    }

    element.handle("Spinner"){
        out.name = "select"
    }

    element.handle("com.lightningkite.butterfly.views.widget.ColorRatingBar"){
        defer("android.widget.RatingBar")
    }

    element.handle("android.widget.RatingBar"){
        out.name = "div"
        out.classes += "khrysalis-rating-bar"
        out.style["display"] = "flex"
        out.style["flex-direction"] = "row"
        out.style["justify-content"] = "center"
        out.style["align-items"] = "center"

        when (rule.allAttributes["style"]) {
            "?android:attr/ratingBarStyle" -> {
                out.classes += "khrysalis-rating-bar-big"
            }
            "?android:attr/ratingBarStyleIndicator" -> {
                out.classes += "khrysalis-rating-bar-reg"
            }
            "?android:attr/ratingBarStyleSmall" -> {
                out.classes += "khrysalis-rating-bar-small"
            }
            else -> {
                out.classes += "khrysalis-rating-bar-tiny"
            }
        }
    }

    element.handle("AutoCompleteTextView") {
        out.name = "div"
        out.classes += "khrysalis-autocomplete"
        val i = ResultNode("input").apply {
            name = "input"
            attributes["type"] = "text"
            style["font-size"] = "12pt"
        }
        out.contentNodes.add(i)
        out.text = i
        out.primary = i
    }

    element.handle("CheckBox"){
        out.name = "label"
        out.classes += "khrc"
        out.style["display"] = "flex"
        out.style["flex-direction"] = "row"
        out.style["align-items"] = rule.allAttributes["android:gravity"]?.let { verticalGravity(it)?.alignDirection }?.let {
            when(it){
                AlignDirection.START -> "flex-start"
                AlignDirection.CENTER -> "center"
                AlignDirection.END -> "flex-end"
            }
        } ?: "center"
        rule.allAttributes["android:gravity"]?.let { horizontalGravity(it)?.alignDirection }?.let {
            when(it){
                AlignDirection.START -> "flex-start"
                AlignDirection.CENTER -> "center"
                AlignDirection.END -> "flex-end"
            }
        }?.let { out.style["justify-content"] = it }
        val input = ResultNode("input").apply {
            if(rule.allAttributes["android:button"] == "@null") {
                style["display"] = "none"
            }
            attributes["type"] = "checkbox"
            if(rule.allAttributes["android:checked"] == "true") {
                attributes["checked"] = "true"
            }
        }
        out.primary = input
        out.contentNodes.add(input)
        val label = ResultNode("div").apply {
            style["flex-grow"] = "1"
            classes.add("khrysalis-label")
        }
        out.contentNodes.add(label)
        out.text = label
        out.containerNode = label
    }

    element.handle("RadioButton"){
        out.name = "label"
        out.classes += "khrc"
        out.style["display"] = "flex"
        out.style["flex-direction"] = "row"
        out.style["align-items"] = rule.allAttributes["android:gravity"]?.let { verticalGravity(it)?.alignDirection }?.let {
            when(it){
                AlignDirection.START -> "flex-start"
                AlignDirection.CENTER -> "center"
                AlignDirection.END -> "flex-end"
            }
        } ?: "center"
        rule.allAttributes["android:gravity"]?.let { horizontalGravity(it)?.alignDirection }?.let {
            when(it){
                AlignDirection.START -> "flex-start"
                AlignDirection.CENTER -> "center"
                AlignDirection.END -> "flex-end"
            }
        }?.let { out.style["justify-content"] = it }
        val input = ResultNode("input").apply {
            if(rule.allAttributes["android:button"] == "@null") {
                style["display"] = "none"
            }
            attributes["type"] = "radio"
            if(rule.allAttributes["android:checked"] == "true") {
                attributes["checked"] = "true"
            }
        }
        out.primary = input
        out.contentNodes.add(input)
        val label = ResultNode("div").apply {
            style["flex-grow"] = "1"
            classes.add("khrysalis-label")
        }
        out.contentNodes.add(label)
        out.text = label
        out.containerNode = label
    }

    element.handle("ToggleButton"){
        out.name = "label"
        out.classes += "khrc"
        out.style["display"] = "flex"
        out.style["flex-direction"] = "row"
        out.style["align-items"] = rule.allAttributes["android:gravity"]?.let { verticalGravity(it)?.alignDirection }?.let {
            when(it){
                AlignDirection.START -> "flex-start"
                AlignDirection.CENTER -> "center"
                AlignDirection.END -> "flex-end"
            }
        } ?: "center"
        rule.allAttributes["android:gravity"]?.let { horizontalGravity(it)?.alignDirection }?.let {
            when(it){
                AlignDirection.START -> "flex-start"
                AlignDirection.CENTER -> "center"
                AlignDirection.END -> "flex-end"
            }
        }?.let { out.style["justify-content"] = it }
        val input = ResultNode("input").apply {
            style["display"] = "none"
            attributes["type"] = "checkbox"
            if(rule.allAttributes["android:checked"] == "true") {
                attributes["checked"] = "true"
            }
        }
        out.primary = input
        out.contentNodes.add(input)
        val label = ResultNode("div").apply {
            style["flex-grow"] = "1"
            classes.add("khrysalis-label")
        }
        out.contentNodes.add(label)
        out.text = label
        out.containerNode = label
    }

    element.handle("Switch"){
        out.name = "label"
        out.classes += "khrc"
        out.classes.add("khrysalis-switch")
        out.style["align-items"] = rule.allAttributes["android:gravity"]?.let { verticalGravity(it)?.alignDirection }?.let {
            when(it){
                AlignDirection.START -> "flex-start"
                AlignDirection.CENTER -> "center"
                AlignDirection.END -> "flex-end"
            }
        } ?: "center"
        rule.allAttributes["android:gravity"]?.let { horizontalGravity(it)?.alignDirection }?.let {
            when(it){
                AlignDirection.START -> "flex-start"
                AlignDirection.CENTER -> "center"
                AlignDirection.END -> "flex-end"
            }
        }?.let { out.style["justify-content"] = it }

        val label = ResultNode("div")
        label.classes.add("khrysalis-label")
        out.text = label
        out.containerNode = label
        out.contentNodes.add(label)

        out.contentNodes.add(ResultNode("input").apply {
            attributes["type"] = "checkbox"
            if(rule.allAttributes["android:checked"] == "true") {
                attributes["checked"] = "true"
            }
        }.also {
            out.primary = it
        })

        out.contentNodes.add(ResultNode("span").apply {
            classes.add("khrysalis-switch-back")
            contentNodes.add(ResultNode("span").apply {
                rule.allAttributes["android:thumbTint"]?.asCssColor()?.let {
                    style["background-color"] = it
                }
                classes.add("khrysalis-switch-front")
            })
        })
    }
}
