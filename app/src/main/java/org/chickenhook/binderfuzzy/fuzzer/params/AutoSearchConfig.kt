package org.chickenhook.binderfuzzy.fuzzer.params

import android.content.Context
import android.util.Log
import org.chickenhook.binderfuzzy.fuzzer.FuzzTask
import org.chickenhook.binderfuzzy.reflectionbrowser.impl.BrowserImpl
import org.chickenhook.binderfuzzy.reflectionbrowser.impl.BrowserImpl.Companion.getValuesRecursive

class AutoSearchConfig(val type: Class<*>) : ParamConfig() {
    override fun getName(): String {
        return "AutoSearchConfig"
    }

    override fun prozessRanges(
        context: Context,
        onFuzzTaskUpdateListener: FuzzTask.OnFuzzTaskUpdateListener
    ): List<ParamConfig> {
        onFuzzTaskUpdateListener.log("Searching for occurences of <$type> in memory")
        val valuesSet = HashSet<Any>() // enforce uniqueness!
        BrowserImpl.getServiceInstances(context).forEach { it ->
            Log.d("AutoSearchConfig", "Searching in $it")
            it?.let {
                val objs = getValuesRecursive(it)
                objs.forEach {
                    val values = BrowserImpl.getValuesOfType(it, typeToSearchFor = type)
                    values.forEach {
                        valuesSet.add((it))
                    }
                }
            }
        }
        val paramConfigs = ArrayList<ParamConfig>()
        valuesSet.forEach {
            paramConfigs.add(AutoSearchConfigValue(it))
        }
        onFuzzTaskUpdateListener.log("Found <${paramConfigs.size}> values")
        return paramConfigs
    }

    override fun getValue(): Any {
        throw IllegalStateException("Use results of processRanges instead")
    }

    class AutoSearchConfigValue(val _value: Any) : ParamConfig() {
        override fun getName(): String {
            return "AutoSearchConfigValue"
        }

        override fun prozessRanges(
            context: Context,
            onFuzzTaskUpdateListener: FuzzTask.OnFuzzTaskUpdateListener
        ): List<ParamConfig> {
            return listOf(this)
        }

        override fun getValue(): Any {
            return _value
        }

        override fun toString(): String {
            return _value.toString()
        }
    }
}