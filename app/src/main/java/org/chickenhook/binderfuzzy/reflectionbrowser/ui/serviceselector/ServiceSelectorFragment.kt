package org.chickenhook.binderfuzzy.reflectionbrowser.ui.serviceselector

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.chickenhook.binderfuzzy.R
import org.chickenhook.binderfuzzy.reflectionbrowser.impl.BrowserImpl
import org.chickenhook.binderfuzzy.reflectionbrowser.ui.serviceselector.items.ClassItem

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [ServiceSelectorFragment.OnListFragmentInteractionListener] interface.
 */
class ServiceSelectorFragment : Fragment() {


    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_service_selector_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager =  LinearLayoutManager(context)
                adapter = MyServiceSelectorRecyclerViewAdapter(
                    asItems(BrowserImpl.getServiceInstances(context)),
                    listener
                )
            }
        }
        return view
    }

    fun asItems(classes: ArrayList<Any>): ArrayList<ClassItem> {
        val itemList = ArrayList<ClassItem>()
        classes.forEach {item->
            item?.let {
                itemList.add(
                    ClassItem(
                        "",
                        it::class.java.simpleName,
                        it::class.java.name,
                        it
                    )
                )
            }
        }
        return itemList;
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: ClassItem?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance() =
            ServiceSelectorFragment().apply {
                arguments = Bundle()
            }
    }
}
