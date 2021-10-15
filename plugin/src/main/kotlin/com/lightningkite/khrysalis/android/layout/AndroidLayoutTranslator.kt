package com.lightningkite.khrysalis.android.layout

import com.lightningkite.khrysalis.replacements.*
import com.lightningkite.khrysalis.xml.*
import org.w3c.dom.Element

abstract class AndroidLayoutTranslator(val replacements: Replacements, val resources: AndroidResources) {
    open fun convertElement(owner: Element, element: Element): Element {
        val allAttributes =
            element.attributeMap + (element["style"]?.let { resources.read(it) as? AndroidStyle }?.map ?: mapOf())
        var rule = replacements.getElement(element.tagName, allAttributes)
            ?: throw IllegalArgumentException("No rule ${element.tagName} found")
        while(rule.deferTo != null) {
            rule = replacements.getElement(rule.deferTo!!, allAttributes)
                ?: throw IllegalArgumentException("No rule ${element.tagName} found")
        }
        val rawText = rule.template.parts.joinToString("") {
            when (it) {
                is TemplatePart.Text -> it.string
                is TemplatePart.Parameter -> it.name.split('.').let {
                    resources.read(element.getAttribute(it[0]))[it.getOrNull(1) ?: "value"]
                }
                else -> ""
            }
        }
        val newElement = owner.appendFragment(rawText)

        // Handle children
        handleChildren(rule, newElement, element)

        // Handle attributes
        for ((key, raw) in allAttributes) {
            handleAttribute(element, newElement, key, raw)
        }
        return newElement
    }

    open fun handleChildren(
        rule: ElementReplacement,
        newElement: Element,
        element: Element
    ) {
        rule.insertChildrenAt?.let { path ->
            val target =
                newElement.xpathElement(path) ?: throw IllegalArgumentException("No element found for path '$path'")
            for (child in element.children.mapNotNull { it as? Element }) {
                convertElement(target, child)
            }
        }
    }

    open fun handleAttribute(
        element: Element,
        newElement: Element,
        key: String,
        raw: String
    ) {
        val value = resources.read(raw)
        val attributeRule = replacements.getAttribute(element.tagName, key, value.type) ?: return
        fun inflateTemplate(template: Template) = template.parts.joinToString("") {
            when (it) {
                is TemplatePart.Text -> it.string
                is TemplatePart.Value -> value["value"]
                is TemplatePart.Parameter -> value[it.name]
                else -> ""
            }
        }
        for ((path, sub) in attributeRule.rules) {
            val target = newElement.xpathElement(path)!!
            if (sub.css.isNotEmpty()) {
                val broken = target.getAttribute("style").split(';')
                    .associate { it.substringBefore(':').trim() to it.substringAfter(':').trim() }
                val resultCss = broken + sub.css.mapValues { inflateTemplate(it.value) }
                target.setAttribute("style", resultCss.entries.joinToString("; ") { "${it.key}: ${it.value}" })
            }
            for (toAppend in sub.append) {
                val content = inflateTemplate(toAppend)
                if (content.startsWith('<'))
                    target.appendFragment(content)
                else
                    target.appendText(content)
            }
            for ((attKey, attTemplate) in sub.attribute) {
                target.setAttribute(attKey, inflateTemplate(attTemplate))
            }
        }
    }
}
