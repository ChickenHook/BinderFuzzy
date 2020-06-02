package org.chickenhook.binderfuzzy.fuzzer.params

import android.content.Context
import org.chickenhook.binderfuzzy.fuzzer.FuzzTask

abstract class ParamConfig {
    abstract fun getName(): String
    abstract fun prozessRanges(
        context: Context,
        onFuzzTaskUpdateListener: FuzzTask.OnFuzzTaskUpdateListener
    ): List<ParamConfig>

    abstract fun getValue() : Any?
}