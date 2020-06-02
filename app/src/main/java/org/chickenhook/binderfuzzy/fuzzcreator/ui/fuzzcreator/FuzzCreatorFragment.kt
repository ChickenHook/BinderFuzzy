package org.chickenhook.binderfuzzy.fuzzcreator.ui.fuzzcreator

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import org.chickenhook.binderfuzzy.R
import org.chickenhook.binderfuzzy.fuzzcreator.FuzzCreatorManager
import org.chickenhook.binderfuzzy.fuzzer.FuzzTask
import org.chickenhook.binderfuzzy.reflectionbrowser.impl.BrowserImpl
import java.lang.reflect.Method
import java.lang.reflect.Parameter

class FuzzCreatorFragment : Fragment() {

    companion object {
        const val ARG_CLASS = "class"
        const val ARG_METHOD_NAME = "method-name"
        const val ARG_HOST_ID = "host-id"

        fun newInstance(clazz: Class<Any>, methodName: String, hostId: Int): FuzzCreatorFragment {
            return FuzzCreatorFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_CLASS, clazz)
                    putString(ARG_METHOD_NAME, methodName)
                    putInt(ARG_HOST_ID, hostId)
                }
            }
        }
    }

    private lateinit var clazz: Class<Any>
    private var method: Method? = null
    private var hostObj: Any? = null
    private var listener: OnFuzzCreatorInteractionListener? = null
    private lateinit var fuzzTask: FuzzTask

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFuzzCreatorInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnParameterSelectedListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { it ->
            clazz = it.getSerializable(ARG_CLASS) as Class<Any>
            hostObj = BrowserImpl.getObjectById(it.getInt(ARG_HOST_ID))
            method = getMethod(clazz, it.getString(ARG_METHOD_NAME))
            method?.let {
                fuzzTask = FuzzCreatorManager.newTask(hostObj!!, it)
            }
        }
    }

    fun getMethod(clazz: Class<Any>, methodName: String?): Method? {
        clazz.declaredMethods.forEach {
            if (it.toGenericString() == methodName) {
                return it
            }
        }
        clazz.methods.forEach {
            if (it.toGenericString() == methodName) {
                return it
            }
        }
        return null;
    }

    fun getMethodParameterItems(method: Method): ArrayList<ParameterItem> {
        val params = ArrayList<ParameterItem>()
        method.parameters.forEach {
            Log.d("FuzzCreatorFragment", "PUT ITEM <" + it!!.name + ">")

            val parameterItem = ParameterItem()
            parameterItem.parameter = it
            params.add(parameterItem)
        }
        return params
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val v = inflater.inflate(R.layout.fuzz_creator_fragment, container, false)
        v.findViewById<Button>(R.id.fuzz_creator_fragment_start).setOnClickListener {
            listener?.onStart(fuzzTask)
        }
        val functionNameView = v.findViewById<TextView>(R.id.function_name)
        if (method == null) {
            functionNameView.text = "[ERROR]"
        } else {
            functionNameView.text = method!!.toGenericString()
        }

        val paramsView = v.findViewById<ListView>(R.id.params_list)
        val items = getMethodParameterItems(method!!)
        val adapter: ArrayAdapter<ParameterItem> =
            object : ArrayAdapter<ParameterItem>(
                context!!,
                R.layout.parameter_item,
                items
            ) {
                override fun getView(
                    position: Int,
                    cv: View?,
                    parent: ViewGroup
                ): View {
                    val view =
                        if (cv == null) {
                            val layoutInflater =
                                context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                            layoutInflater.inflate(R.layout.parameter_item, parent, false)
                        } else {
                            cv
                        }
                    Log.d(
                        "FuzzCreatorFragment",
                        "ADD ITEM <" + items[position].parameter!!.name + ">"
                    )
                    view.findViewById<TextView>(R.id.parameter_item_title)?.text =
                        items[position].parameter!!.name
                    view.findViewById<TextView>(R.id.parameter_item_type)?.text =
                        items[position].parameter!!.type.simpleName
                    return view;
                }

            }
        paramsView.adapter = adapter
        paramsView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                listener?.onParameterSelected(fuzzTask.id, id.toInt())
            }
        return v;
    }

    class ParameterItem {
        var parameter: Parameter? = null
    }

    interface OnFuzzCreatorInteractionListener {
        fun onParameterSelected(fuzzId: Int, paramId: Int) // todo FuzzObject!!
        fun onStart(fuzzTask: FuzzTask)
    }
}
