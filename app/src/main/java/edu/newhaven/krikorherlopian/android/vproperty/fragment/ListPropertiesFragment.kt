package edu.newhaven.krikorherlopian.android.vproperty.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.adapter.RecylerViewAdapter
import edu.newhaven.krikorherlopian.android.vproperty.fragmentActivityCommunication
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.ListClick
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import kotlinx.android.synthetic.main.listview_fragment.view.*

class ListPropertiesFragment : Fragment(), ListClick {
    var root: View? = null
    lateinit var adapter: RecylerViewAdapter
    var list: MutableList<Any> = mutableListOf<Any>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.listview_fragment, container, false)
        var param = arguments?.getString(ARG_PARAM)
        var section = arguments?.getInt(ARG_SECTION_NUMBER)
        if (section == 1) {
            getData(section, param!!)
        } else if (section == 2) {
            getData(section, param!!)
        } else if (section == 3) {
            getData(section, param!!)
        } else {
            getData(section!!, param!!)
        }
        return root
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"
        private const val ARG_PARAM = "param"
        /**   private const val ARG_SECTION_NUMBER = "section_number"
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int, param: String): ListPropertiesFragment {
            return ListPropertiesFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                    putString(ARG_PARAM, param)
                }
            }
        }
    }

    private fun getData(param: Int = 0, param2: String = "") {
        val db = FirebaseFirestore.getInstance()
        var count = 0
        list.clear()
        root?.text?.visibility = View.VISIBLE
        adapter = RecylerViewAdapter(
            list, this, root?.context!!, true
        )
        root?.recyclerView?.apply {
            layoutManager = GridLayoutManager(context, 2)
        }
        // root?.recyclerView?.layoutManager = LinearLayoutManager(root?.context)
        root?.recyclerView?.itemAnimator = DefaultItemAnimator()
        root?.recyclerView?.adapter = adapter

        var docRef = db.collection("properties").whereEqualTo("homeFacts.homeType", param2)

        when (param) {
            1 -> docRef = db.collection("properties")
            2 -> docRef = db.collection("properties").whereEqualTo("homeFacts.sale", true)
            3 -> docRef = db.collection("properties").whereEqualTo("homeFacts.rent", true)
            else -> docRef = db.collection("properties").whereEqualTo("homeFacts.homeType", param2)
        }


        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            for (doc in snapshot!!.documentChanges) {
                when (doc.type) {
                    DocumentChange.Type.MODIFIED -> {
                        var prop: Property = doc.document.toObject(Property::class.java)
                        var i = 0
                        var inList: Boolean = false
                        for (propert in list) {
                            if (doc.document.id.equals((propert as Property).id)) {
                                inList = true
                                prop.id = doc.document.id
                                list[i] = prop
                            }
                            i = i + 1
                        }
                        if (inList == false && prop.isDisabled.trim().equals(
                                "N"
                            )
                        ) {
                            prop.id = doc.document.id
                            list.add(0, prop)
                            var manager = root?.recyclerView?.layoutManager!! as LinearLayoutManager
                            val firstItem = manager.findFirstVisibleItemPosition()
                            val firstItemView = manager.findViewByPosition(firstItem)
                            val topOffset = firstItemView?.top?.toFloat()
                            adapter.notifyItemRangeInserted(0, 1)
                            adapter.notifyItemRangeChanged(0, list.size)
                        } else if (inList == true && prop.isDisabled.trim().equals("Y")) {
                            var manager = root?.recyclerView?.layoutManager!! as LinearLayoutManager
                            list.remove(prop)
                            val firstItem = manager.findFirstVisibleItemPosition()
                            val firstItemView = manager.findViewByPosition(firstItem)
                            val topOffset = firstItemView?.top?.toFloat()
                            adapter.notifyItemRangeRemoved(0, 1)
                            adapter.notifyItemRangeChanged(0, list.size)
                        } else
                            adapter.notifyDataSetChanged()
                    }
                    DocumentChange.Type.ADDED -> {
                        var prop: Property = doc.document.toObject(Property::class.java)
                        if (prop.isDisabled.trim().equals(
                                "N"
                            )
                        ) {
                            prop.id = doc.document.id
                            list.add(0, prop)
                            var manager = root?.recyclerView?.layoutManager!! as LinearLayoutManager
                            val firstItem = manager.findFirstVisibleItemPosition()
                            val firstItemView = manager.findViewByPosition(firstItem)
                            val topOffset = firstItemView?.top?.toFloat()
                            adapter.notifyItemRangeInserted(0, 1)
                            adapter.notifyItemRangeChanged(0, list.size)
                        }

                    }
                }
            }
            if (list.size > 0)
                root?.text?.visibility = View.GONE
            else
                root?.text?.visibility = View.VISIBLE


        }
    }

    override fun rowClicked(position: Int, position2: Int, imageLayout: ImageView?) {
        fragmentActivityCommunication?.startActivityDetWithTransition(
            (list.get(position) as Property),
            imageLayout!!
        )
    }

    override fun deleteRow(position: Int) {
    }

}