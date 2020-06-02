package org.chickenhook.binderfuzzy.fuzzer.params

import android.content.Context
import android.content.Intent
import org.chickenhook.binderfuzzy.fuzzer.FuzzTask

class ConstantIntentConfig(val intent: Intent) : ParamConfig() {
    override fun getName(): String {
        return "ConstantIntentConfig"
    }

    override fun prozessRanges(
        context: Context,
        onFuzzTaskUpdateListener: FuzzTask.OnFuzzTaskUpdateListener
    ): List<ParamConfig> {
        return listOf(this)
    }

    override fun getValue(): Any? {
        return intent
    }
}