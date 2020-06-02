package org.chickenhook.binderfuzzy.fuzzer.params

import android.content.Context
import org.chickenhook.binderfuzzy.fuzzer.FuzzTask

class NullConfig : ParamConfig() {
    override fun getName(): String {
        return "NullConfig"
    }

    override fun prozessRanges(
        context: Context,
        onFuzzTaskUpdateListener: FuzzTask.OnFuzzTaskUpdateListener
    ): List<ParamConfig> {
        return listOf(this)
    }

    override fun getValue(): Any? {
        return null
    }
}