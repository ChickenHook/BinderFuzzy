package org.chickenhook.binderfuzzy.fuzzcreator

import org.chickenhook.binderfuzzy.fuzzer.FuzzTask
import java.lang.reflect.Method

object FuzzCreatorManager {
    val fuzzTasks = HashMap<Int, FuzzTask>()

    var currId = 0

    fun newTask(obj:Any, method: Method): FuzzTask {
        val task = FuzzTask(currId, obj, method)
        fuzzTasks.put(currId, task)
        currId++
        return task
    }

    fun getTaskById(id: Int): FuzzTask? {
        return fuzzTasks[id]
    }
}