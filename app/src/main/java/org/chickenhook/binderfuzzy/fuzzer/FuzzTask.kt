package org.chickenhook.binderfuzzy.fuzzer

import android.content.Context
import android.os.SystemClock
import org.chickenhook.binderfuzzy.fuzzer.params.ParamConfig
import org.chickenhook.binderfuzzy.utils.InvokeWorkaround
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class FuzzTask(val id: Int, val hostObj: Any, val method: Method) {

    val paramConfigs = HashMap<Int, HashMap<String, ParamConfig>>()
    lateinit var context: Context
    lateinit var onFuzzTaskUpdateListener: OnFuzzTaskUpdateListener

    fun execute(context: Context, onFuzzTaskUpdateListener: OnFuzzTaskUpdateListener) {
        this.context = context
        this.onFuzzTaskUpdateListener = onFuzzTaskUpdateListener
        val obj = hostObj
        log("Call: ${method.toGenericString()}")
        log("Configurations: ${paramConfigs.size}")
        if (paramConfigs.size != method.parameterCount) {
            log("Parameters not configured... abort!")
            return;
        }
//        log("Process configuration..")
        var success = 0
        var curr = 0
        try {
            processConfigsAndRun {
                curr++
                if (perform(obj, it)) {
                    success++
                    log("Successful <${curr}>")
                }
            }
            log("Successful runs $success/${curr}}")
        } catch (ex: Exception) {
            log("Error while execute fuzzing task", ex)
        }

//        val configSets = ArrayList<ConfigurationSet>()
//        if (paramConfigs.size == 0) {
//            configSets.add(ConfigurationSet())
//        } else {
//            configSets.addAll(processConfigs(ArrayList()))
//        }
//        log("Performing <${configSets.size}> fuzzing runs..")
//        var success = 0
//        configSets.forEachIndexed { i, it ->
//            log(">> ${i + 1}/${configSets.size} <<")
//            if (perform(obj, it)) {
//                success++
//                log("Successful <${i + 1}>")
//            }
////            SystemClock.sleep(50)//TODO configure
//        }
//        log("Successful runs $success/${configSets.size}")
    }

    fun processConfigsAndRun(callback: (ConfigurationSet) -> Unit): Long {
        log("Preparing fuzz configuration")
        // prepare
        var indizies = Array<Long>(paramConfigs.size) { 0L }
        val lengths = Array<Long>(paramConfigs.size) { 0L }
        val values = Array<ArrayList<ParamConfig>>(paramConfigs.size) { ArrayList() }
        paramConfigs.forEach { it ->
            val processedList = ArrayList<ParamConfig>()
            it.value.forEach {
                processedList.addAll(it.value.prozessRanges(context, onFuzzTaskUpdateListener))
            }
            lengths[it.key] = processedList.size.toLong()
            values[it.key] = processedList
            indizies[it.key] = 0
        }
        // calculate rounds
        var prod = 1L
        lengths.forEach {
            prod *= it
        }
        log("Performing <${prod}> fuzzing iterations..")
        SystemClock.sleep(1000) // give some time for reading!
        // perform
        var iteration = 0L;
        while (iteration < prod) {
            // create current params
            val currParams = Array<Any?>(paramConfigs.size) { null }
            indizies.forEachIndexed { i, it ->
                currParams[i] = values[i][it.toInt()].getValue()
            }
            // callback
            val c = ConfigurationSet()
            c.params.addAll(currParams)
            callback(c)

            // increment
            iteration++
            increment(lengths, indizies, iteration)
        }
        return prod
    }

    fun increment(
        lengths: Array<Long>,
        indizies: Array<Long>,
        iteration: Long
    ) {
        var currIndex = 0
        var rest = iteration
        while (currIndex < indizies.size) {

            if (rest != 0L) {
                var divisor = 1
                if (currIndex < indizies.size - 1) {
                    for (i in currIndex + 1 until indizies.size) {
                        divisor *= lengths[i].toInt()
                    }
                }
                val quotient:Long = rest / divisor
                indizies[currIndex] = quotient
                rest -= quotient * divisor
            } else {
                indizies[currIndex] = 0
            }
            currIndex++
        }
//        indizies.forEachIndexed { i, it ->
//            log("Index at $i = $it")
//        }
//        if (iteration > lengths[currIndex] * factor) {
//            increment(lengths, indizies, iteration, currIndex - 1)
//        }
    }

    fun processConfigs(
        configSets: ArrayList<ConfigurationSet>,
        paramId: Int = paramConfigs.size - 1
    ): ArrayList<ConfigurationSet> {
        if (paramId == 0) { // create single list
            paramConfigs[paramId]?.values?.forEach {
                val configs = it.prozessRanges(context, onFuzzTaskUpdateListener)
                configs.forEach {
                    val configurationSet = ConfigurationSet()
                    configurationSet.params.add(it.getValue())
                    configSets.add(configurationSet)
                }
            }
            return configSets;
        }
        val list = processConfigs(configSets, paramId - 1)
        val newList = ArrayList<ConfigurationSet>()
        list.forEach { existingSet -> // for every list create new single list
            paramConfigs[paramId]?.values?.forEach {
                it.prozessRanges(context, onFuzzTaskUpdateListener).forEach {
                    val configSet = ConfigurationSet()
                    configSet.params.addAll(existingSet.params)
                    configSet.params.add(it)
                    newList.add(configSet)
                }
            }
        }
        return newList
    }

    private fun perform(service: Any, configurationSet: ConfigurationSet): Boolean {
        method.isAccessible = true
        try {
            log("Do invoke ${method.name}(${configurationSet.params.joinToString()})")
            val res =
                InvokeWorkaround.invoke(
                    method,
                    service,
                    configurationSet.params.toTypedArray()
                )
            log("Got result $res")
        } catch (exception: InvocationTargetException) {
//            log("=> skipping due to:", exception.targetException)
            log("=> skipping due to: " + exception.targetException.message)
            return false
        }
        return true
    }


    interface OnFuzzTaskUpdateListener {
        fun log(message: String)
    }

    class ConfigurationSet { // represents configuration for one single iteration
        val params = ArrayList<Any?>()
    }

    fun log(message: String) {
        onFuzzTaskUpdateListener.log(message)
    }

    fun log(message: String, exception: Throwable) {
        val sw = StringWriter()
        exception.printStackTrace(PrintWriter(sw))
        val exceptionAsString: String = sw.toString()
        onFuzzTaskUpdateListener.log(message + "\n" + exceptionAsString)
    }
}