package org.chickenhook.binderfuzzy.fuzzer.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.activity_fuzzer_console.*
import org.chickenhook.binderfuzzy.R
import org.chickenhook.binderfuzzy.fuzzcreator.FuzzCreatorManager
import org.chickenhook.binderfuzzy.fuzzer.FuzzTask
import org.chickenhook.binderfuzzy.fuzzer.FuzzerExecutor
import org.chickenhook.binderfuzzy.fuzzer.script.Parser
import org.chickenhook.binderfuzzy.utils.toFullString
import java.io.File


class FuzzerConsole : AppCompatActivity(), FuzzTask.OnFuzzTaskUpdateListener {

    companion object {
        const val ARG_FUZZ_ID = "fuzz-id"
        const val ARG_LOG_FILE = "log-file"
    }

    var fuzzId = -1
    var messages = ArrayList<String>()

    @Volatile
    var exceptionRow = 0

    @Volatile
    var updateRow = 0
    var adapter: ArrayAdapter<String>? = null

    @Volatile
    var amount = 0L
    var exceptionTypes = HashSet<Class<out Any>>()

    @Volatile
    var success = 0L

    @Volatile
    var failed = 0L

    var logFile: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fuzzer_console)
        intent.extras?.let {

            logFile = it.getString(ARG_LOG_FILE, "")
            fuzzId = it.getInt(ARG_FUZZ_ID, -1)
        }
        if (logFile == "") {
            launchTask()
        } else if (fuzzId >= 0) {
            logToUi("File: $logFile")
            Thread() {
                logFile?.let {
                    File(it).forEachLine {
                        logToUi(it)
                    }
                }

            }.start()
        } else {
            try {
                fuzzId = Parser.parseAndRegisterTask(this).id
                launchTask()
            } catch (exception: Exception) {
                logToUi(exception.toFullString())
            }
        }

        adapter = initListView(console_output)
    }

    fun launchTask() {
        logToUi("Initialize task $fuzzId")
        FuzzCreatorManager.getTaskById(fuzzId)?.let {
            FuzzerExecutor.enqueue(it, this, this)
        } ?: run {
            log("=> no task fund with id <$fuzzId>")
        }
    }

    fun initListView(view: ListView): ArrayAdapter<String> {
        view.isTextFilterEnabled = true;
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, messages)
        view.adapter = adapter
        return adapter
    }

    fun logToUi(message: String) {
        runOnUiThread {
            messages.add(message)
            adapter?.notifyDataSetChanged()
//                    console_output.setSelection(adapter.count - 1);
        }
    }

    override fun log(message: String) {
        if (amount == 0L) {
            logToUi(message)
        }
        Log.d("FuzzerConsole", message)
    }

    override fun fail(id: Long, exception: Throwable) {
        updateCounter(id, amount, success, failed++)
        val exceptionType = exception::class.java
        if (!exceptionTypes.contains(exceptionType)) {
            addException((exceptionType.simpleName + ": " + exception.message))
            exceptionTypes.add(exception::class.java)
        }
    }

    override fun success(id: Long, params: String) {
        updateCounter(id, amount, success++, failed)
        logToUi(params)
    }

    override fun onStart(amount: Long) {
        this.amount = amount
        updateCounter(0, amount, 0, 0)
        addException("Exceptions:")
        logToUi("Successful params:")
    }

    fun updateCounter(curr: Long, max: Long, success: Long, failed: Long) {
        runOnUiThread {
            val str = "Running $curr/$max\n(success=$success, failed=$failed)"
            if (updateRow == 0) {
                updateRow = messages.size
                messages.add(str)
            } else {
                messages[updateRow] = str
            }
            adapter?.notifyDataSetChanged()
        }
    }

    fun addException(message: String) {
        runOnUiThread {
            if (exceptionRow == 0) {
                exceptionRow = messages.size
                messages.add(message)
            } else {
                messages[exceptionRow] = messages[exceptionRow] + "\n\n" + message
            }
            adapter?.notifyDataSetChanged()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu items for use in the action bar
        val v = getMenuInflater().inflate(R.menu.search_menu, menu);
//        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val search: SearchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    adapter?.filter?.filter(it);
                    adapter?.notifyDataSetChanged();
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    adapter?.filter?.filter(it);
                    adapter?.notifyDataSetChanged();
                }
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }
}
