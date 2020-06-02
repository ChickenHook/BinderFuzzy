package org.chickenhook.binderfuzzy.fuzzcreator

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.chickenhook.binderfuzzy.R
import org.chickenhook.binderfuzzy.fuzzcreator.ui.fuzzcreator.FuzzCreatorFragment
import org.chickenhook.binderfuzzy.fuzzcreator.ui.paramconfig.ParamConfigurator
import org.chickenhook.binderfuzzy.fuzzer.FuzzTask
import org.chickenhook.binderfuzzy.fuzzer.ui.FuzzerConsole

class FuzzCreator : AppCompatActivity(), FuzzCreatorFragment.OnFuzzCreatorInteractionListener,
    ParamConfigurator.ParamConfiguratorListener {

    companion object {
        const val ARG_METHOD_NAME = "ARG_METHOD_NAME"
        const val ARG_CLASS_NAME = "ARG_CLASS_NAME"
        const val ARG_HOST_ID = "ARG_HOST_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fuzz_creator_activity)

        intent?.extras?.let {
            if (intent.hasExtra(ARG_CLASS_NAME) && intent.hasExtra(ARG_METHOD_NAME)) {
                if (savedInstanceState == null) {

                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.container, FuzzCreatorFragment.newInstance(
                                it.get(
                                    ARG_CLASS_NAME
                                ) as Class<Any>,
                                it.getString(
                                    ARG_METHOD_NAME
                                )!!,
                                it.getInt(ARG_HOST_ID)
                            )
                        )
                        .commitNow()
                }
            }
        }

    }

    override fun onParameterSelected(fuzzId: Int, paramId: Int) {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.container, ParamConfigurator.newInstance(
                    fuzzId, paramId
                )
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onStart(fuzzTask: FuzzTask) {
        val startIntent = Intent(this, FuzzerConsole::class.java)
        startIntent.putExtra(FuzzerConsole.ARG_FUZZ_ID, fuzzTask.id)
        startActivity(startIntent)
    }

    override fun onFinish() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            Log.i("FuzzCreator", "popping backstack")
            supportFragmentManager.popBackStack()
        }
    }
}
