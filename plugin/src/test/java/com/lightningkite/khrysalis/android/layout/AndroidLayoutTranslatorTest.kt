package com.lightningkite.khrysalis.android.layout

import com.lightningkite.khrysalis.replacements.Replacements
import com.lightningkite.khrysalis.swift.KotlinSwiftCR
import com.lightningkite.khrysalis.xml.*
import org.junit.Assert.*
import org.junit.Test
import org.w3c.dom.Element
import java.io.File

class AndroidLayoutTranslatorTest {
    @Test fun testXpath() {
        """<test><child/></test>""".readXml().documentElement.xpathNode("child")
            .also { println(it) }
            .also { println((it ?: 0)::class.qualifiedName) }
            .also { println(it is Element) }
    }

    @Test
    fun quickTest() {
        val xml = """
            <ScrollView xmlns:android="http://schemas.android.com/apk/res/android" xmlns:tools="http://schemas.android.com/tools"
                    android:id="@+id/scrollView"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent">

                <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="vertical"
                        android:padding="8dp">
            
                    <FrameLayout android:layout_width="match_parent" android:layout_height="200dp">
                        <TextView
                                style="@style/Body"
                                android:background="#FF8080"
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:layout_margin="8dp"
                                android:padding="16dp"
                                android:layout_gravity="left|top"
                                android:text="left|top"/>
                        <TextView
                                style="@style/Body"
                                android:background="#FF8080"
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:layout_margin="8dp"
                                android:padding="16dp"
                                android:textColor="#F00"
                                android:layout_gravity="left|bottom"
                                android:text="left|bottom"/>
                    </FrameLayout>
                </LinearLayout>
            </ScrollView>
        """.readXml()

        println(xml.writeToString())
        val replacements = Replacements(KotlinSwiftCR.replacementMapper)
        replacements += """
---
- id: ScrollView
  type: element
  template: <sv></sv>
  insertChildrenAt: ""
- id: LinearLayout
  type: element
  template: <ll></ll>
  insertChildrenAt: ""
- id: FrameLayout
  type: element
  template: <fl></fl>
  insertChildrenAt: ""
- id: TextView
  type: element
  template: <tv></tv>
- id: "android:text"
  type: attribute
  element: TextView
  rules:
    "":
      append: [~value~]
- id: "android:textColor"
  type: attribute
  element: TextView
  valueType: Color
  rules:
    "":
      attribute:
        color: ~android~

        """
        println(replacements.elements.values.flatMap { it }.joinToString("\n"))
        println(replacements.attributes.values.flatMap { it }.joinToString("\n"))

        val resources = AndroidResources()
        val projectBase = File("../../../khrysalis-template/android/src/main/res")
        resources.parse(projectBase)

        val converter = object : AndroidLayoutTranslator(replacements, resources) {}
        val doc = buildXmlDocument("base") {
            converter.convertElement(this, xml.documentElement)
        }
        println(doc.writeToString())
    }
}