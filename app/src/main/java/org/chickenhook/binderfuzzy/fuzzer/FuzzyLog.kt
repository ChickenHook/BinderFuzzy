package org.chickenhook.binderfuzzy.fuzzer

import android.content.Context
import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.concurrent.Executors

class FuzzyLog {

    val logExecutor= Executors.newSingleThreadExecutor()

    var bufferedWriter: BufferedWriter? = null


    fun open(context: Context, filename: String): File {
        val path = generatePath(context, filename)
        Log.i("FuzzyLog", "Open log file at $path")
        bufferedWriter = BufferedWriter(FileWriter(path))
        return path
    }

    fun close() {
        logExecutor.submit {
            bufferedWriter?.close()
        }
    }

    fun log(message: String) {
        logExecutor.submit {
            bufferedWriter?.write(message)
            bufferedWriter?.flush()
        }
    }

    fun generatePath(context: Context, filename: String): File {
        val f =
            File(context.getExternalFilesDir(null)!!.absolutePath + File.separator + "logs" + File.separator + filename + ".log")
        f.parentFile.mkdirs()
        return f
    }
}