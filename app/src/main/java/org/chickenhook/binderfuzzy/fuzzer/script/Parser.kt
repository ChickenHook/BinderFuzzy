package org.chickenhook.binderfuzzy.fuzzer.script

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import org.chickenhook.binderfuzzy.fuzzcreator.FuzzCreatorManager
import org.chickenhook.binderfuzzy.fuzzer.FuzzTask
import org.chickenhook.binderfuzzy.fuzzer.params.*
import java.io.File
import java.io.FileReader
import java.lang.reflect.Method

class Parser {

    // TODO refactor and improve logging / error handling
    companion object {

        const val DEFAULT_SCRIPT_NAME = "script.bf"

        fun parseAndRegisterTask(context: Context): FuzzTask { // : Fuz
            Log.i("Parser", "Trying to read config from file!")// Task
            context.getExternalFilesDir(null)?.let {
                val path = File(it.absolutePath + File.separator + DEFAULT_SCRIPT_NAME)
                if (path.exists()) {
                    return parseFromJson(path)
                } else {
                    throw IllegalArgumentException("No config file found at: ${path.absolutePath}")
                }
            }
            throw IllegalArgumentException("Unknown error")

        }

        fun parseFromJson(path: File): FuzzTask {
            val config = Gson().fromJson(FileReader(path), FuzzyConfig::class.java)

            var curr_obj: Any? = null
            Log.d("Parser", "Got config $config")
            config.fields_ordered?.forEach {
                if (it.clazz == null) {
                    throw java.lang.IllegalArgumentException("please specify a clazz in your fields_ordered list item")
                }
                if (it.field == null) {
                    throw java.lang.IllegalArgumentException("please specify a field in your fields_ordered list item")
                }
                val clz = Class.forName(it.clazz!!)
                val field = clz.getDeclaredField(it.field!!)
                field.isAccessible = true
                curr_obj = field.get(curr_obj)
            }

            if (curr_obj == null) {
                throw java.lang.IllegalArgumentException("could not find host object")
            }

            if (config.call == null) {
                throw java.lang.IllegalArgumentException("please specify a call element")
            }
            if (config.call!!.clazz == null) {
                throw java.lang.IllegalArgumentException("please specify a class name to call")
            }
            val clz = Class.forName(config.call!!.clazz!!)
            clz.declaredMethods.forEach {
                if (it.toGenericString().contains(config.call!!.method!!)) {
                    val task = register(curr_obj!!, it)
                    it.parameters.forEachIndexed { i, it ->
                        val paramConfig = HashMap<String, ParamConfig>()
                        val conigCls = configByName(config.call!!.params!![i])
                        conigCls?.let { clazz ->
                            if (conigCls.name == AutoSearchConfig::class.java.name) {
                                val config = AutoSearchConfig(it.type)
                                paramConfig[config.getName()] = config
                            } else {
                                val config = conigCls.newInstance() as ParamConfig
                                paramConfig[config.getName()] = config
                            }
                        } ?: kotlin.run {
                            val config = AutoSearchConfig(it.type)
                            paramConfig[config.getName()] = config
                        }

                        task.paramConfigs[i] = paramConfig
                    }
                    return task
                }
            }
            throw java.lang.IllegalArgumentException("Did not find method <${config.call!!.method}> to be called!")
        }

        fun configByName(name: String?): Class<*>? {
            when (name) {
                "auto" -> return AutoSearchConfig::class.java
                "packageNames" -> return PackageNamesConfig::class.java
                "launchIntents" -> return LaunchIntentsConfig::class.java
                "null" -> return NullConfig::class.java
            }
            return null
        }


        fun register(obj: Any, m: Method): FuzzTask {
            return FuzzCreatorManager.newTask(obj, m)
        }
    }
}