package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.swift.replacements.SwiftImport
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import java.util.*
import kotlin.collections.ArrayList

private val weakAfter = WeakHashMap<KtFile, ArrayList<()->Unit>>()
val KtFile.after: MutableList<()->Unit>
    get() = weakAfter.getOrPut(this) { ArrayList() }

fun SwiftTranslator.registerFile() {
    handle<KtFile> {
        typedRule.importDirectives.forEach {
            if(it.importPath?.isAllUnder == true) {
                val p = it.importPath!!.pathStr.removeSuffix("*")
                this@registerFile.fqToImport.entries
                    .asSequence()
                    .filter {
                        it.key.startsWith(p) && !it.key.substringAfter(p).contains('.')
                    }
                    .map { it.value }
                    .distinct()
                    .forEach {
                        out.addImport(SwiftImport(it))
                    }
            }
            it.importedReference?.text?.let {
                this@registerFile.fqToImport[it]?.let {
                    out.addImport(SwiftImport(it))
                }
            }
        }
        typedRule.allChildren.dropWhile { it !is KtDeclaration }.forEach {
            -it
        }
        -"\n"
        while(typedRule.after.isNotEmpty()){
            val copy = typedRule.after.toList()
            typedRule.after.clear()
            for(item in copy){
                item()
            }
        }
    }
}
