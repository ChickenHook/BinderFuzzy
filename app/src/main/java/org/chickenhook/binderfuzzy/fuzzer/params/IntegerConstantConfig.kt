package org.chickenhook.binderfuzzy.fuzzer.params

import android.content.Context
import org.chickenhook.binderfuzzy.fuzzer.FuzzTask

class IntegerConstantConfig(val constant: Int) : ParamConfig() {
    override fun getName(): String {
        return IntegerConstantConfig::class.java.simpleName
    }

    override fun prozessRanges(
        context: Context,
        onFuzzTaskUpdateListener: FuzzTask.OnFuzzTaskUpdateListener
    ): List<ParamConfig> {
        return listOf(this)
    }

    override fun getValue(): Any {
        return constant
    }

    override fun toString(): String {
        return "IntegerConstantConfig(constant=$constant)"
    }

}