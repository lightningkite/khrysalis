package com.lightningkite.khrysalis.intellij

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project

@Service
class KhrysalisModuleService(val module: Project) : /*PersistentStateComponent<>,*/ com.intellij.openapi.Disposable {
    companion object {
        fun getInstance(project: Project): KhrysalisModuleService =
            project.getService(KhrysalisModuleService::class.java)
    }

    override fun dispose() {
    }

}