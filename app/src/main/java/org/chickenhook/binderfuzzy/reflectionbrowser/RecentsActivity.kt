package org.chickenhook.binderfuzzy.reflectionbrowser

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_recents.*
import org.chickenhook.binderfuzzy.R
import org.chickenhook.binderfuzzy.fuzzer.ui.FuzzerConsole
import org.chickenhook.binderfuzzy.storage.AppDatabase
import org.chickenhook.binderfuzzy.storage.FuzzyTaskInfo

class RecentsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recents)
        Thread() {
            val tasks = getAllTasks()
            runOnUiThread {
                initListView(recents_list, tasks)
            }
        }.start()
    }


    fun initListView(view: ListView, tasks: List<FuzzyTaskInfo>): ArrayAdapter<String> {
        view.isTextFilterEnabled = true;
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, asItems(tasks))
        view.adapter = adapter
        view.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, FuzzerConsole::class.java)
            tasks[position]?.logFile?.let {
                intent.putExtra(FuzzerConsole.ARG_LOG_FILE, it)
                startActivity(intent)
            }

        }
        return adapter
    }

    fun asItems(list: List<FuzzyTaskInfo>): ArrayList<String> {
        val strings = ArrayList<String>()
        list.forEach {
            it.taskName?.let { it1 -> strings.add(it1) }
        }
        return strings
    }

    fun getAllTasks(): List<FuzzyTaskInfo> {
        return AppDatabase.get(this.applicationContext).fuzzyTaskInfoDao().getAll()
    }
}