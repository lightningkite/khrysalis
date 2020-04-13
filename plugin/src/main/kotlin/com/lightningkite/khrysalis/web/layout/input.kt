package com.lightningkite.khrysalis.web.layout

import com.lightningkite.khrysalis.utils.AlignDirection
import com.lightningkite.khrysalis.utils.horizontalGravity
import com.lightningkite.khrysalis.utils.verticalGravity
import java.util.concurrent.atomic.AtomicInteger

val idNumber = AtomicInteger(0)

internal fun HtmlTranslator.input() {
    element.handle("EditText"){
        out.name = "input"
        out.attributes["type"] = "text"
        out.style["font-size"] = "12pt"
    }
    element.handle("CheckBox"){
        out.name = "div"
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
        val id = "id${idNumber.getAndIncrement()}"
        val input = ResultNode("input").apply {
            if(rule.allAttributes["android:button"] == "@null") {
                style["display"] = "none"
            }
            attributes["id"] = id
            attributes["type"] = "checkbox"
            attributes["value"] = id
        }
        out.contentNodes.add(input)
        val label = ResultNode("label").apply {
            attributes["for"] = id
            style["flex-grow"] = "1"
        }
        out.contentNodes.add(label)
        out.primary = label
    }
    element.handle("RadioButton"){
        out.name = "div"
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
        val id = "id${idNumber.getAndIncrement()}"
        val input = ResultNode("input").apply {
            if(rule.allAttributes["android:button"] == "@null") {
                style["display"] = "none"
            }
            attributes["id"] = id
            attributes["name"] = run {
                var current = out.parent
                while(current != null){
                    current.other["RadioGroupId"]?.let { it as? String }?.let {
                        return@run it
                    }
                    current = current.parent
                }
                return@run id
            }
            attributes["type"] = "radio"
            attributes["value"] = id
        }
        out.contentNodes.add(input)
        val label = ResultNode("label").apply {
            attributes["for"] = id
            style["flex-grow"] = "1"
        }
        out.contentNodes.add(label)
        out.primary = label
    }
}
