package org.chickenhook.binderfuzzy.fuzzer.params

import android.content.Context
import org.chickenhook.binderfuzzy.fuzzer.FuzzTask

class IntegerRangeConfig(val from: Int, val to: Int) : ParamConfig() {
    override fun getName(): String {
        return IntegerConstantConfig::class.java.simpleName
    }

    override fun prozessRanges(
        context: Context, onFuzzTaskUpdateListener: FuzzTask.OnFuzzTaskUpdateListener
    ): List<ParamConfig> {
        val paramConfigList = ArrayList<ParamConfig>()
        for (i in from until to) {
            paramConfigList.add(IntegerConstantConfig(i))
        }
        return paramConfigList;
    }

    override fun getValue(): Any {
        throw IllegalStateException("Use results of processRanges instead")
    }
}