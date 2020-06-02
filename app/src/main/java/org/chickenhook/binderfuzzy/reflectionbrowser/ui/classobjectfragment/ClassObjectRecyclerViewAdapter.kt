package org.chickenhook.binderfuzzy.reflectionbrowser.ui.classobjectfragment


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_class_object.view.*
import org.chickenhook.binderfuzzy.R
import org.chickenhook.binderfuzzy.reflectionbrowser.ui.classobjectfragment.ClassObjectFragment.OnListFragmentInteractionListener
import org.chickenhook.binderfuzzy.reflectionbrowser.ui.classobjectfragment.items.ClassMemberItem
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * [RecyclerView.Adapter] that can display a [ClassMemberItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class ClassObjectRecyclerViewAdapter(
    private val mValues: List<ClassMemberItem>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<ClassObjectRecyclerViewAdapter.ViewHolder>(), Filterable {

    private val mOnClickListener: View.OnClickListener
    protected var list: List<ClassMemberItem> = mValues
    protected var originalList: List<ClassMemberItem> = mValues

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as ClassMemberItem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_class_object, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.mIdView.text = item.id
        holder.mContentView.text = item.content

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mContentView: TextView = mView.content

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(
                constraint: CharSequence,
                results: FilterResults
            ) {
                list = results.values as List<ClassMemberItem>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {
                var filteredResults: List<ClassMemberItem>? = null
                filteredResults = if (constraint.isEmpty()) {
                    originalList
                } else {
                    getFilteredResults(constraint.toString().toLowerCase())
                }
                val results = FilterResults()
                results.values = filteredResults
                return results
            }
        }
    }

    protected fun getFilteredResults(constraint: String?): List<ClassMemberItem>? {
        val results: MutableList<ClassMemberItem> = ArrayList()
        constraint?.let {
            for (item in originalList!!) {
                if (item.member.name.toLowerCase().contains(constraint)) {
                    results.add(item)
                }
                if (item.member is Method) {
                    if (item.member.toGenericString().toLowerCase().contains(constraint)) {
                        results.add(item)
                    }
                } else if (item.member is Field) {
                    if (item.member.toGenericString().toLowerCase().contains(constraint)) {
                        results.add(item)
                    }
                }
            }
            return results
        }
        return results
    }
}
