package com.lightningkite.kwift.layoutxml

data class ViewType(
    val androidName: String,
    val iosName: String,
    val extendsAndroidName: String? = null,
    val myConfiguration: Appendable.(XmlNode) -> Unit = {}
) {
    companion object {

        val bindings = HashMap<String, String>()

        val registry = HashMap<String, ViewType>()
        val default = ViewType(
            "UnknownView",
            "UIView",
            "View"
        ) { appendln("//Unrecognized type ${it.name}") }

        fun register(type: ViewType) {
            registry[type.androidName] = type
        }

        fun register(
            androidName: String,
            iosName: String,
            extendsAndroidName: String? = null,
            myConfiguration: Appendable.(XmlNode) -> Unit = {}
        ) {
            val type = ViewType(
                androidName = androidName,
                iosName = iosName,
                extendsAndroidName = extendsAndroidName,
                myConfiguration = myConfiguration
            )
            registry[type.androidName] = type
        }

        fun write(appendable: Appendable, node: XmlNode) {
            println("Writing ${node.name}")
            for(att in node.attributes){
                println("    ${att.key} = ${att.value}")
            }
            (registry[node.name] ?: default).write(appendable, node)
        }

        init {
            setupNormalViewTypes()
        }
    }

    fun writeConfiguration(appendable: Appendable, node: XmlNode) {
        extendsAndroidName?.let {
            registry[it]!!.myConfiguration(appendable, node)
        }
        myConfiguration.invoke(appendable, node)
    }

    fun write(appendable: Appendable, node: XmlNode) {
        appendable.appendln("{ () -> $iosName in ")
        appendable.appendln("let view = $iosName(frame: .zero)")
        writeConfiguration(appendable, node)
        appendable.appendln("return view")
        appendable.appendln("}()")
    }
}
