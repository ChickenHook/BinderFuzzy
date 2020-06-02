package org.chickenhook.binderfuzzy.fuzzcreator.ui.paramconfig

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import org.chickenhook.binderfuzzy.R
import org.chickenhook.binderfuzzy.R.id.param_configurator_integer_const
import org.chickenhook.binderfuzzy.fuzzcreator.FuzzCreatorManager
import org.chickenhook.binderfuzzy.fuzzer.FuzzTask
import org.chickenhook.binderfuzzy.fuzzer.params.*
import java.lang.reflect.Parameter


class ParamConfigurator : Fragment() {

    companion object {
        const val TAG = "ParamConfigurator"
        const val ARG_FUZZ_ID = "args-fuzz-id"
        const val ARG_PARAM_ID = "args-param-id"
        fun newInstance(fuzzId: Int, paramId: Int): ParamConfigurator {
            return ParamConfigurator().apply {
                arguments = Bundle().apply {
                    putInt(ARG_FUZZ_ID, fuzzId)
                    putInt(ARG_PARAM_ID, paramId)
                }
            }
        }
    }

    val items = ArrayList<GenericParamConfigItem>()
    private lateinit var parameter: Parameter
    private var fuzzId: Int = 0
    private var paramId: Int = 0
    private lateinit var fuzzTask: FuzzTask
    private var listener: ParamConfiguratorListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ParamConfiguratorListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement ParamConfiguratorListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);

        arguments?.let {
            fuzzId = it.getInt(ARG_FUZZ_ID)
            paramId = it.getInt(ARG_PARAM_ID)
            fuzzTask = FuzzCreatorManager.getTaskById(fuzzId)!!
            parameter = fuzzTask.method.parameters[paramId]
            Log.d(TAG, "Initialized with task <$fuzzId> for param <$paramId> of type <$parameter>")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (parameter.type == Integer::class.java || parameter.type == Integer.TYPE) {
            return inflateInteger(inflater, container, savedInstanceState)
        } else if (parameter.type == String::class.java) {
            return inflateString(inflater, container, savedInstanceState)
        } else if(parameter.type == Intent::class.java){
            return inflateIntent(inflater, container, savedInstanceState)
        }
        return inflateGeneric(inflater, container, savedInstanceState);
    }

    fun inflateInteger(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.param_configurator_generic, container, false)
        items.add(IntegerRangeItem())
        items.add(IntegerConstantItem())
        items.add(AutoSearchItem(parameter))
        initListView(v.findViewById(R.id.param_config_selector), items);
        return v;
    }

    fun inflateGeneric(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.param_configurator_generic, container, false)
        items.add(AutoSearchItem(parameter))
        items.add(NullItem())
        initListView(v.findViewById(R.id.param_config_selector), items);
        return v;
    }

    fun inflateString(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.param_configurator_generic, container, false)
        items.add(AutoSearchItem(parameter))
        items.add(NullItem())
        items.add(PackageNamesItem())
        initListView(v.findViewById(R.id.param_config_selector), items);
        return v;
    }

    fun inflateIntent(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.param_configurator_generic, container, false)
        items.add(AutoSearchItem(parameter))
        items.add(NullItem())
        items.add(LaunchIntentsItem())
        initListView(v.findViewById(R.id.param_config_selector), items);
        return v;
    }

    fun inflateBinder(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.param_configurator_generic, container, false)
        items.add(AutoSearchItem(parameter))
        initListView(v.findViewById(R.id.param_config_selector), items);
        return v;
    }

    fun initListView(configList: ListView, items: ArrayList<GenericParamConfigItem>) {
        val adapter: ArrayAdapter<GenericParamConfigItem> =
            object : ArrayAdapter<GenericParamConfigItem>(
                context!!,
                R.layout.parameter_item,
                items
            ) {
                override fun getView(
                    position: Int,
                    cv: View?,
                    parent: ViewGroup
                ): View {
                    val layoutInflater =
                        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    return items[position].getView(layoutInflater, parent)
                }

            }
        configList.adapter = adapter
    }

    open abstract class GenericParamConfigItem() {
        abstract fun getView(
            layoutInflater: LayoutInflater, parent: ViewGroup
        ): View;

        abstract fun getConfiguration(): ParamConfig?
    }

    open class AutoSearchItem(val param: Parameter) : GenericParamConfigItem() {
        var v: View? = null
        lateinit var checkBox: CheckBox

        override fun getView(layoutInflater: LayoutInflater, parent: ViewGroup): View {
            if (v != null) return v!!
            v = layoutInflater.inflate(R.layout.param_configurator_auto_search, parent, false)
            checkBox = v!!.findViewById(R.id.param_configurator_auto_search_checkbox)
            return v!!
        }

        override fun getConfiguration(): ParamConfig? {
            if (checkBox.isChecked) {
                return AutoSearchConfig(param.type)
            } else {
                return null
            }
        }

    }

    open class LaunchIntentsItem() : GenericParamConfigItem() {
        var v: View? = null
        lateinit var checkBox: CheckBox

        override fun getView(layoutInflater: LayoutInflater, parent: ViewGroup): View {
            if (v != null) return v!!
            v = layoutInflater.inflate(R.layout.param_configurator_null, parent, false)
            v!!.findViewById<TextView>(R.id.paramconfigurator_null_title)?.let{
                it.text="Collect launch items of all apps"
            }
            checkBox = v!!.findViewById(R.id.param_configurator_null_checkbox)
            return v!!
        }

        override fun getConfiguration(): ParamConfig? {
            if (checkBox.isChecked) {
                return LaunchIntentsConfig()
            } else {
                return null
            }
        }
    }

    open class PackageNamesItem() : GenericParamConfigItem() {
        var v: View? = null
        lateinit var checkBox: CheckBox

        override fun getView(layoutInflater: LayoutInflater, parent: ViewGroup): View {
            if (v != null) return v!!
            v = layoutInflater.inflate(R.layout.param_configurator_null, parent, false)
            checkBox = v!!.findViewById(R.id.param_configurator_null_checkbox)
            v!!.findViewById<TextView>(R.id.paramconfigurator_null_title)?.let{
                it.text="Collect package names of all apps"
            }
            return v!!
        }

        override fun getConfiguration(): ParamConfig? {
            if (checkBox.isChecked) {
                return PackageNamesConfig()
            } else {
                return null
            }
        }
    }

    open class NullItem() : GenericParamConfigItem() {
        var v: View? = null
        lateinit var checkBox: CheckBox

        override fun getView(layoutInflater: LayoutInflater, parent: ViewGroup): View {
            if (v != null) return v!!
            v = layoutInflater.inflate(R.layout.param_configurator_null, parent, false)
            checkBox = v!!.findViewById(R.id.param_configurator_null_checkbox)
            return v!!
        }

        override fun getConfiguration(): ParamConfig? {
            if (checkBox.isChecked) {
                return NullConfig()
            } else {
                return null
            }
        }
    }

    open class IntegerRangeItem() : GenericParamConfigItem() {
        lateinit var from: EditText
        lateinit var to: EditText
        var v: View? = null

        override fun getView(layoutInflater: LayoutInflater, parent: ViewGroup): View {
            if (v != null) return v!!
            v = layoutInflater.inflate(R.layout.param_configurator_integer_range, parent, false)
            from = v!!.findViewById(R.id.param_configurator_integer_range_from)
            to = v!!.findViewById(R.id.param_configurator_integer_range_to)
            return v!!
        }

        override fun getConfiguration(): ParamConfig? {
            return try {
                IntegerRangeConfig(
                    from.text.toString().toInt()
                    ,
                    to.text.toString().toInt()
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    open class IntegerConstantItem() : GenericParamConfigItem() {
        lateinit var constantView: EditText
        var v: View? = null


        override fun getView(layoutInflater: LayoutInflater, parent: ViewGroup): View {
            if (v != null) return v!!
            v = layoutInflater.inflate(R.layout.param_configurator_integer_const, parent, false)
            constantView = v!!.findViewById(param_configurator_integer_const)
            Log.d("DEBUG", "GET VIEW!")
            return v!!
        }

        override fun getConfiguration(): ParamConfig? {
            return try {
                IntegerConstantConfig(
                    constantView.text.toString().toInt()
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.param_configurator_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.param_configurator_menu_save) {
            val paramConfig = HashMap<String, ParamConfig>()
            items.forEach { config ->
                config.getConfiguration()?.let {
                    paramConfig[it.getName()] = it
                }
            }
            fuzzTask.paramConfigs[paramId] = paramConfig
            listener?.onFinish()
        }
        return super.onOptionsItemSelected(item)
    }

    interface ParamConfiguratorListener {
        fun onFinish()
    }
}
