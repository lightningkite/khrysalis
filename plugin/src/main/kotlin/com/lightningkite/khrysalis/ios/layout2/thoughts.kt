package com.lightningkite.khrysalis.ios.layout2

import com.lightningkite.khrysalis.ios.layout.readXMLStyles
import com.lightningkite.khrysalis.utils.XmlNode
import java.io.File


fun main() {
    convertLayoutsToSwift2(
        androidFolder = File("/home/joseph/IdeaProjects/layout-tester-butterfly/android"),
        iosFolder = File("/home/joseph/IdeaProjects/layout-tester-butterfly/ios/LayoutTesting"),
        equivalentsFolders = sequenceOf(File("/home/joseph/IdeaProjects/khrysalis-meta/butterfly-ios"))
    )
}