package org.chickenhook.binderfuzzy.reflectionbrowser.ui.classobjectfragment

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.chickenhook.binderfuzzy.R
import org.chickenhook.binderfuzzy.reflectionbrowser.ReflectionBrowser
import org.chickenhook.binderfuzzy.reflectionbrowser.impl.BrowserImpl
import org.chickenhook.binderfuzzy.reflectionbrowser.ui.classobjectfragment.items.ClassMemberItem
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * Displays members of the given object.
 *
 * Once the user clicked a member we call our callback OnListFragmentInteractionListener.
 */
class ClassObjectFragment : Fragment() {

    // TODO: Customize parameters
    private lateinit var obj: Any

    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var _adapter: ClassObjectRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);

        arguments?.let {
            obj = BrowserImpl.getObjectById(it.getInt(ARG_OBJ_ID))!!
            setTitle(obj::class.java.name)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_class_object_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter =
                    ClassObjectRecyclerViewAdapter(
                        asItems(BrowserImpl.getMembers(obj::class.java)),
                        listener
                    )
                _adapter = adapter as ClassObjectRecyclerViewAdapter
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
    }

    private fun setTitle(title: String) {
        getActionBar()?.title = title
    }

    private fun getActionBar(): ActionBar? {
        return (activity as ReflectionBrowser?)?.supportActionBar
    }

    /**
     * Convert class members into items that can be filled into the ListView.
     *
     * @param members the members to be converted
     */
    fun asItems(members: List<Member>): List<ClassMemberItem> {
        val classMemberItem = ArrayList<ClassMemberItem>()
        members.forEach {
            if (it is Field) {
                it.isAccessible = true
                classMemberItem.add(
                    ClassMemberItem(
                        it::class.java.simpleName + "  ",
                        it.toGenericString() + " = " + it.get(obj),
                        it::class.java.simpleName,
                        it,
                        obj
                    )
                )
            } else if (it is Method) {
                classMemberItem.add(
                    ClassMemberItem(
                        it::class.java.simpleName,
                        it.toGenericString(),
                        it::class.java.simpleName,
                        it,
                        obj
                    )
                )
            }

        }
        return classMemberItem
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.search_menu, menu)
        val search: SearchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    _adapter?.filter?.filter(it);
                    _adapter?.notifyDataSetChanged();
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    _adapter?.filter?.filter(it);
                    _adapter?.notifyDataSetChanged();
                }
                return true
            }

        })
        super.onCreateOptionsMenu(menu, inflater)
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
        fun onListFragmentInteraction(item: ClassMemberItem?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_OBJ_ID = "obj-id"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(objId: Int) =
            ClassObjectFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_OBJ_ID, objId)
                }
            }
    }
}
