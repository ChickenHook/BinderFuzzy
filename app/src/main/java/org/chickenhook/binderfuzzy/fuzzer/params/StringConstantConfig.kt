package org.chickenhook.binderfuzzy.fuzzer.params

import android.content.Context
import org.chickenhook.binderfuzzy.fuzzer.FuzzTask

class StringConstantConfig(val string: String) : ParamConfig() {
    override fun getName(): String {
        return "StringConstantConfig"
    }

    override fun prozessRanges(
        context: Context,
        onFuzzTaskUpdateListener: FuzzTask.OnFuzzTaskUpdateListener
    ): List<ParamConfig> {
        return listOf(this)
    }

    override fun getValue(): Any? {
        return string
    }
}