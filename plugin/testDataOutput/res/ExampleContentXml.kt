//
// ExampleContentXml.swift
// Created by Kwift XML Android
//
package Users.josephivie.IdeaProjects.kwift.testDataOutput.res

import android.widget.*
import android.view.*
import com.lightningkite.kwift.views.actual.*
import com.lightningkite.kwift.views.shared.*
import com.lightningkite.kwifttest.R

class ExampleContentXml {

    lateinit var exampleContentNumber: TextView
    lateinit var exampleContentIncrement: Button
    lateinit var exampleContentGoToAnotherScreen: Button

    fun setup(dependency: ViewDependency): View {
        val view = LayoutInflater.from(dependency.context).inflate(R.layout.example_content, null, false)
        exampleContentNumber = view.findViewById<TextView>(R.id.exampleContentNumber)
        exampleContentIncrement = view.findViewById<Button>(R.id.exampleContentIncrement)
        exampleContentGoToAnotherScreen = view.findViewById<Button>(R.id.exampleContentGoToAnotherScreen)
        return view
    }
}