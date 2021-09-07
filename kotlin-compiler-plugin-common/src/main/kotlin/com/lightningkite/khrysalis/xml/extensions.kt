package com.lightningkite.khrysalis.xml

import com.fasterxml.jackson.databind.node.TextNode
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.File
import java.io.StringReader
import java.io.StringWriter
import java.util.*
import java.util.function.Consumer
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathExpression
import javax.xml.xpath.XPathFactory

val defaultBuilder = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder()

operator fun Element.get(key: String): String = this.getAttribute(key)
operator fun Element.set(key: String, value: String) = this.setAttribute(key, value)
val Element.attributeMap: AttributesMap get() = AttributesMap(this)
val Element.children: List<Node> get() = NodeListList(this.childNodes)

class AttributesMap(val element: Element): MutableMap<String, String> {
    override val size: Int
        get() = element.attributes.length

    override fun containsKey(key: String): Boolean = element.attributes.getNamedItem(key) != null

    override fun containsValue(value: String): Boolean = (0 until element.attributes.length).any { element.attributes.item(it).nodeValue == value }

    override fun get(key: String): String? = element.getAttribute(key)

    override fun isEmpty(): Boolean = size == 0

    class Entry(val attribute: Node): MutableMap.MutableEntry<String, String> {
        override val key: String
            get() = attribute.nodeName
        override val value: String
            get() = attribute.nodeValue

        override fun toString(): String = "$key: $value"
        override fun setValue(newValue: String): String { val old = attribute.nodeValue; attribute.nodeValue = newValue; return old }
    }

    override val entries: MutableSet<MutableMap.MutableEntry<String, String>>
        get() = (0 until element.attributes.length).map { Entry(element.attributes.item(it)) }.toMutableSet()
    override val keys: MutableSet<String>
        get() = (0 until element.attributes.length).map { element.attributes.item(it).nodeName }.toMutableSet()
    override val values: MutableCollection<String>
        get() = (0 until element.attributes.length).map { element.attributes.item(it).nodeValue }.toMutableList()

    override fun clear() {
        for(key in keys.toList())
            element.attributes.removeNamedItem(key)
    }

    override fun put(key: String, value: String): String? {
        element.setAttribute(key, value)
        return null
    }

    override fun putAll(from: Map<out String, String>) {
        for(entry in from.entries){
            put(entry.key, entry.value)
        }
    }

    override fun remove(key: String): String? {
        element.removeAttribute(key)
        return null
    }
}

class NodeListList(val set: NodeList): List<Node> {
    override val size: Int
        get() = set.length

    override fun contains(element: Node): Boolean = any { it == element }
    override fun containsAll(elements: Collection<Node>): Boolean = elements.all { it in this }
    override fun get(index: Int): Node = set.item(index)
    override fun indexOf(element: Node): Int = indexOfFirst { it == element }
    override fun isEmpty(): Boolean = set.length == 0
    override fun iterator(): Iterator<Node> = DeferIterator(this)
    override fun lastIndexOf(element: Node): Int = indexOfLast { it == element }
    override fun listIterator(): ListIterator<Node> = DeferIterator(this)
    override fun listIterator(index: Int): ListIterator<Node> = DeferIterator(this, index)
    override fun subList(fromIndex: Int, toIndex: Int): List<Node> = (fromIndex until toIndex).map { set.item(it) }
}

class DeferIterator<E>(val parentList: List<E>, var cursor: Int = 0): ListIterator<E> {
    var lastRet = -1

    override fun hasNext(): Boolean {
        return this.cursor != parentList.size
    }

    override fun next(): E {
        val i: Int = this.cursor
        return if (i >= parentList.size) {
            throw NoSuchElementException()
        } else {
            if (i >= parentList.size) {
                throw ConcurrentModificationException()
            } else {
                this.cursor = i + 1
                parentList[i.also { lastRet = it }]
            }
        }
    }

    fun forEachRemaining(action: Consumer<in E>) {
        Objects.requireNonNull(action)
        val size: Int = parentList.size
        var i: Int = this.cursor
        if (i < size) {
            if (i >= parentList.size) {
                throw ConcurrentModificationException()
            }
            while (i < size) {
                action.accept(parentList[i])
                ++i
            }
            this.cursor = i
            lastRet = i - 1
        }
    }

    override fun hasPrevious(): Boolean {
        return cursor != 0
    }

    override fun nextIndex(): Int {
        return cursor
    }

    override fun previousIndex(): Int {
        return cursor - 1
    }

    override fun previous(): E {
        val i = cursor - 1
        return if (i < 0) {
            throw NoSuchElementException()
        } else {
            if (i >= parentList.size) {
                throw ConcurrentModificationException()
            } else {
                cursor = i
                parentList[i.also { lastRet = it }]
            }
        }
    }
}

fun Element.appendFragment(fragment: String): Element {
    val node = defaultBuilder.parse(InputSource(StringReader(fragment))).documentElement
    ownerDocument.adoptNode(node)
    this.appendChild(node)
    return node
}
fun Element.appendElement(name: String) = appendChild(ownerDocument.createElement(name)) as Element
fun Element.appendText(text: String) = appendChild(ownerDocument.createTextNode(text))
inline fun Element.appendElement(name: String, setup: Element.()->Unit): Element = appendChild(ownerDocument.createElement(name).apply(setup)) as Element

fun NodeList.fix(): NodeListList = NodeListList(this)
inline fun XPathExpression.evaluateNodeSet(on: Any): NodeList? = evaluate(on, XPathConstants.NODESET) as? NodeList
inline fun XPathExpression.evaluateNode(on: Any): Node? = evaluate(on, XPathConstants.NODE) as? Node

fun NodeList.firstOrNull(): Node? = if(length > 0) item(0) else null

fun Element.xpathNode(path: String): Node? {
    if(path.isEmpty()) return this
    return XPathFactory.newDefaultInstance().newXPath().compile(path).evaluateNode(this)
}
fun Element.xpathElement(path: String): Element? {
    if(path.isEmpty()) return this
    return XPathFactory.newDefaultInstance().newXPath().compile(path).evaluateNode(this) as? Element
}

fun String.readXml(): Document {
    return defaultBuilder.parse(this.toByteArray().inputStream())
}
fun File.readXml(): Document {
    return defaultBuilder.parse(this)
}
fun File.writeXml(document: Document) = this.bufferedWriter().use { writer ->
    TransformerFactory
        .newInstance()
        .newTransformer()
        .transform(DOMSource(document), StreamResult(writer))
}
fun Document.writeToString(): String = StringWriter().use {
    TransformerFactory
        .newInstance()
        .newTransformer()
        .transform(DOMSource(this), StreamResult(it))
    it.toString()
}

inline fun buildXmlDocument(name: String, action: Element.()->Unit): Document {
    val document = defaultBuilder.newDocument()
    val element = document.createElement(name)
    document.appendChild(element)
    action(element)
    return document
}
operator fun Element.plusAssign(other: Node) {
    this.appendChild(other)
}
