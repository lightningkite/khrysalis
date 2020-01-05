package com.lightningkite.kwift.layout

import com.lightningkite.kwift.utils.XmlNode

data class ViewType(
    val androidName: String,
    val iosName: String,
    val extendsAndroidName: String? = null,
    val handlesPadding: Boolean = false,
    val iosConstructor: String = "$iosName(frame: .zero)",
    val myConfiguration: OngoingLayoutConversion.(XmlNode) -> Unit = {}
) {
    companion object {
        fun mapOf(vararg viewTypes: ViewType): Map<String, ViewType> {
            return viewTypes.associate { it.androidName to it }
        }

//        val bindings = HashMap<String, String>()
//
//        val skipTypes = HashSet<String>()
//        val registry = HashMap<String, ViewType>()
//
        fun default(node: XmlNode): ViewType {
            val newViewType = ViewType(
                node.name,
                node.name.substringAfterLast('.'),
                "View"
            ) { }
            return newViewType
        }
//
//        fun register(type: ViewType) {
//            registry[type.androidName] = type
//        }
//
//        fun register(
//            androidName: String,
//            iosName: String,
//            extendsAndroidName: String? = null,
//            myConfiguration: Appendable.(XmlNode) -> Unit = {}
//        ) {
//            val type = ViewType(
//                androidName = androidName,
//                iosName = iosName,
//                extendsAndroidName = extendsAndroidName,
//                myConfiguration = myConfiguration
//            )
//            registry[type.androidName] = type
//        }
//
//        init {
//            setupNormalViewTypes()
//        }
    }

    fun writeConfiguration(appendable: OngoingLayoutConversion, node: XmlNode) {
        extendsAndroidName?.let {
            appendable.converter.viewTypes.getValue(it).writeConfiguration(appendable, node)
        }
        myConfiguration.invoke(appendable, node)
    }

    fun write(appendable: OngoingLayoutConversion, node: XmlNode) {
        appendable.appendln("{ () -> $iosName in ")
        appendable.appendln("let view = $iosConstructor")
        writeConfiguration(appendable, node)
        appendable.appendln("return view")
        appendable.appendln("}()")
    }
}
