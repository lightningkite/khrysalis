package com.lightningkite.khrysalis.ios

import com.lightningkite.khrysalis.utils.copyOutFromRes
import java.io.File

fun setUpIosProject(target: File, organization: String, organizationId: String, projectName: String) {
    copyOutFromRes("ios", target, organization, organizationId, projectName)
}
