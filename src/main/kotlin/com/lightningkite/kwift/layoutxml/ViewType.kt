package com.lightningkite.kwift.layoutxml

data class ViewType(
    val androidName: String,
    val iosName: String,
    val extendsAndroidName: String? = null,
    val myConfiguration: Appendable.(XmlNode) -> Unit = {}
) {
    companion object {

        val bindings = HashMap<String, String>()

        val skipTypes = HashSet<String>()
        val registry = HashMap<String, ViewType>()
        fun default(node: XmlNode): ViewType {
            val newViewType = ViewType(
                node.name,
                "CustomView" + node.name.substringAfterLast('.'),
                "View"
            ) { }
            register(newViewType)
            return newViewType
        }

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
            if(node.name in skipTypes){
                for(child in node.children){
                    write(appendable, child)
                }
            } else {
                (registry[node.name] ?: default(node)).write(appendable, node)
            }
        }

        init {
            setupNormalViewTypes()
        }
    }

    fun writeConfiguration(appendable: Appendable, node: XmlNode) {
        extendsAndroidName?.let {
            registry[it]!!.writeConfiguration(appendable, node)
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
