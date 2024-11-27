package com.mkroo.lmsdemo.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.IsolationMode
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode

class KotestConfig : AbstractProjectConfig() {
    override val isolationMode = IsolationMode.InstancePerLeaf
    override fun extensions(): List<Extension> {
        return super.extensions() + SpringTestExtension(SpringTestLifecycleMode.Root)
    }
}