package org.chickenhook.binderfuzzy.fuzzer.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.activity_fuzzer_console.*
import org.chickenhook.binderfuzzy.R
import org.chickenhook.binderfuzzy.fuzzcreator.FuzzCreatorManager
import org.chickenhook.binderfuzzy.fuzzer.FuzzTask
import org.chickenhook.binderfuzzy.fuzzer.FuzzerExecutor


class FuzzerConsole : AppCompatActivity(), FuzzTask.OnFuzzTaskUpdateListener {

    companion object {
        const val ARG_FUZZ_ID = "fuzz-id"
    }

    var fuzzId = 0
    var messages = ArrayList<String>()
    var adapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fuzzer_console)
        intent.extras?.let {
            fuzzId = it.getInt(ARG_FUZZ_ID)
        }
        log("Initialize task $fuzzId")
        FuzzCreatorManager.getTaskById(fuzzId)?.let {
            FuzzerExecutor.enqueue(it, this, this)
        } ?: run {
            log("=> no task fund with id <$fuzzId>")
        }
        adapter = initListView(console_output)
    }

    fun initListView(view: ListView): ArrayAdapter<String> {
        view.isTextFilterEnabled = true;
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, messages)
        view.adapter = adapter
        return adapter
    }

    override fun log(message: String) {
        Log.d("FuzzerConsole", message)
        runOnUiThread {
            messages.add(message)
            adapter?.notifyDataSetChanged()
//                    console_output.setSelection(adapter.count - 1);
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
