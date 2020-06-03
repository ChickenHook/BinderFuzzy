package org.chickenhook.binderfuzzy.fuzzer

import android.content.Context
import android.os.SystemClock
import android.util.Log
import org.chickenhook.binderfuzzy.fuzzer.params.ParamConfig
import org.chickenhook.binderfuzzy.storage.AppDatabase
import org.chickenhook.binderfuzzy.storage.FuzzyTaskInfo
import org.chickenhook.binderfuzzy.utils.InvokeWorkaround
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FuzzTask(val id: Int, val hostObj: Any, val method: Method) {

    val fuzzyLog = FuzzyLog()
    val paramConfigs = HashMap<Int, HashMap<String, ParamConfig>>()
    lateinit var context: Context
    lateinit var onFuzzTaskUpdateListener: OnFuzzTaskUpdateListener

    /**
     * Executes this task
     */
    fun execute(context: Context, onFuzzTaskUpdateListener: OnFuzzTaskUpdateListener) {
        this.context = context
        this.onFuzzTaskUpdateListener = onFuzzTaskUpdateListener
        val obj = hostObj
        log("Call: ${method.toGenericString()}")
        log("Configurations: ${paramConfigs.size}")

        try {
            insertTaskEntry()
            if (paramConfigs.size != method.parameterCount) {
                log("Parameters not configured... abort!")
                return;
            }
            var success = 0L
            var curr = 0L
            processConfigsAndRun {
                curr++
                try {
                    perform(obj, it)
                    success++
                    onFuzzTaskUpdateListener.success(curr, it.toString())
                    log("Successful <${curr}>")
                } catch (exception: InvocationTargetException) {
                    onFuzzTaskUpdateListener.fail(curr, exception.targetException)
                    log("=> skipping due to: " + exception.targetException.message)
                }
            }
            log("Successful runs $success/${curr}}")
            fuzzyLog.close()
        } catch (ex: Exception) {
            log("Error while execute fuzzing task", ex)
            Log.e("FuzzTask", "error", ex)
        }
    }

    /**
     * Brutes through all config sets and call the given lambda.
     *
     * @param callback will be called for each configuration that should be executed
     */
    fun processConfigsAndRun(callback: (ConfigurationSet) -> Unit): Long {
        log("Preparing fuzz configuration")
        // prepare
        var indizies =
            Array(paramConfigs.size) { 0L } // represents the current config index for each parameter
        val lengths =
            Array(paramConfigs.size) { 0L } // represents the amount of configs for each paramter
        val values =
            Array<ArrayList<ParamConfig>>(paramConfigs.size) { ArrayList() } // list of available configs

        // create list array of config lists. Every parameter must have one configuration list with size>0
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
        onFuzzTaskUpdateListener.onStart(prod)
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

    /**
     * Increment indizies representing the next "iteration".
     *
     * We brute through all available param configs so we have to increase the values for each param.
     *
     * Example (lengths: 10-3-1-5):
     *
     * 1. round
     * 0-0-0-0
     * 2. round
     * 0-0-0-1
     * ...
     * 6. round
     * 0-0-1-0
     * ...
     * 12.round
     * 0-1-0-0
     * ...
     */
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
                val quotient: Long = rest / divisor
                indizies[currIndex] = quotient
                rest -= quotient * divisor
            } else {
                indizies[currIndex] = 0
            }
            currIndex++
        }
    }

    /**
     * Executes a call to the service using the given configurationSet.
     *
     * @param service the object instance to perform the call on
     * @param configurationSet containing the parameter values to be used for the call
     * @return true on success
     */
    private fun perform(service: Any, configurationSet: ConfigurationSet): Boolean {
        method.isAccessible = true
        log("Do invoke ${method.name}(${configurationSet.params.joinToString()})")
        val res =
            InvokeWorkaround.invoke(
                method,
                service,
                configurationSet.params.toTypedArray()
            )
        log("Got result $res")

        return true
    }


    interface OnFuzzTaskUpdateListener {
        fun log(message: String)
        fun fail(id: Long, exception: Throwable)
        fun success(id: Long, params: String)
        fun onStart(amount: Long)
    }

    class ConfigurationSet { // represents configuration for one single iteration
        val params = ArrayList<Any?>()

        override fun toString(): String {
            return "ConfigurationSet(params=${params.joinToString { it.toString() }})"
        }


    }

    fun log(message: String) {
        onFuzzTaskUpdateListener.log(message)
        fuzzyLog.log(message+"\n")
    }

    fun log(message: String, exception: Throwable) {
        val sw = StringWriter()
        exception.printStackTrace(PrintWriter(sw))
        val exceptionAsString: String = sw.toString()
        onFuzzTaskUpdateListener.log(message + "\n" + exceptionAsString+"\n")
        fuzzyLog.log(message + "\n" + exceptionAsString)
        Log.e("FuzzTask", "error", exception)
    }


    ////////////////////////// DATABASE /////////////////////

    fun insertTaskEntry() {
        val name =
            Date().toLocaleString() + "_" + hostObj::class.java.name + "_" + method.name
        AppDatabase.get(context).fuzzyTaskInfoDao().insertAll(
            FuzzyTaskInfo(
                0,
                name,
                hostObj::class.java.name,
                method.toGenericString(),
                fuzzyLog.open(context, name).absolutePath
            )
        )
    }
}