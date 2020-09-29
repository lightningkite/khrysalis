package com.lightningkite.khrysalis.ios.layout2

import com.lightningkite.khrysalis.ios.layout.readXMLStyles
import com.lightningkite.khrysalis.utils.XmlNode
import java.io.File


fun main() {

//    fun makeTextView(text: String) = XibView(
//        type = "label",
//        attributes = mapOf(
//            "text" to text
//        ),
//        properties = listOf()
//    )
//
//    val left = makeTextView("left")
//    val center = makeTextView("center")
//    val right = makeTextView("right")
//    val fill = makeTextView("fill")
//    val root = XibView(type = "view", subviews = listOf(left, center, right, fill))
//    root.constraints = listOf(
//        //Vertical constraints
//        XibConstraint(firstItem = left.id, firstAttribute = "top", secondItem = root.id, secondAttribute = "top", constant = 16.0),
//        XibConstraint(firstItem = center.id, firstAttribute = "top", secondItem = left.id, secondAttribute = "bottom", constant = 16.0),
//        XibConstraint(firstItem = right.id, firstAttribute = "top", secondItem = center.id, secondAttribute = "bottom", constant = 16.0),
//        XibConstraint(firstItem = fill.id, firstAttribute = "top", secondItem = right.id, secondAttribute = "bottom", constant = 16.0),
//
//        //Horizontal constraints
//        XibConstraint(firstItem = left.id, firstAttribute = "leading", secondItem = root.id, secondAttribute = "leading", constant = 16.0),
//        XibConstraint(firstItem = center.id, firstAttribute = "centerX", secondItem = root.id, secondAttribute = "centerX"),
//        XibConstraint(firstItem = right.id, firstAttribute = "trailing", secondItem = root.id, secondAttribute = "trailing", constant = 16.0),
//        XibConstraint(firstItem = fill.id, firstAttribute = "leading", secondItem = root.id, secondAttribute = "leading", constant = 16.0),
//        XibConstraint(firstItem = fill.id, firstAttribute = "trailing", secondItem = root.id, secondAttribute = "trailing", constant = 16.0)
//    )
//    println(
//        XibDocument(
//            owner = XibOwner(
//                className = "GeneratedTest",
//                classModule = "test_layout"
//            ),
//            view = root,
//            resources = listOf()
//        )
//    )
}