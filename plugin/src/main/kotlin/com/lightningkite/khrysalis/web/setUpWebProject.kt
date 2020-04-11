package com.lightningkite.khrysalis.web

import com.lightningkite.khrysalis.ios.swift.SwiftAltListener
import com.lightningkite.khrysalis.utils.copyOutFromRes
import java.io.File

fun setUpWebProject(target: File, organization: String, organizationId: String, projectName: String)
        = copyOutFromRes("web", target, organization, organizationId, projectName)
