//
// MainXml.swift
// Created by Kwift XML Android
//
package Users.josephivie.IdeaProjects.kwift.testDataOutput.res

import android.widget.*
import android.view.*
import com.lightningkite.kwift.views.actual.*
import com.lightningkite.kwift.views.shared.*
import com.lightningkite.kwifttest.R

class MainXml {

    lateinit var mainBack: ImageButton
    lateinit var title: TextView
    lateinit var mainContent: FrameLayout

    fun setup(dependency: ViewDependency): View {
        val view = LayoutInflater.from(dependency.context).inflate(R.layout.main, null, false)
        mainBack = view.findViewById<ImageButton>(R.id.mainBack)
        title = view.findViewById<TextView>(R.id.title)
        mainContent = view.findViewById<FrameLayout>(R.id.mainContent)
        return view
    }
}