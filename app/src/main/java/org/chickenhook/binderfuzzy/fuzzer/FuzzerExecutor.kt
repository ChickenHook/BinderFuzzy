package org.chickenhook.binderfuzzy.fuzzer

import android.content.Context
import java.util.concurrent.Executors

object FuzzerExecutor {

    val executor = Executors.newSingleThreadExecutor()

    fun enqueue(
        fuzzTask: FuzzTask,
        context: Context,
        onFuzzTaskUpdateListener: FuzzTask.OnFuzzTaskUpdateListener
    ) {
        executor.submit {
            fuzzTask.execute(context, onFuzzTaskUpdateListener)
        }
    }
}