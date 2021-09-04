package com.lightningkite.khrysalis.ios.layout2

import com.lightningkite.khrysalis.ios.layout.readXMLStyles
import com.lightningkite.khrysalis.utils.XmlNode
import java.io.File


fun main() {
    convertLayoutsToSwift2(
        androidFolder = File("/home/jivie/Projects/khrysalis-template/android"),
        iosFolder = File("/home/jivie/Projects/khrysalis-template/ios/ButterflyTemplate"),
        equivalentsFolders = sequenceOf(File("/home/jivie/Projects/khrysalis-meta/butterfly-ios"))
    )
}