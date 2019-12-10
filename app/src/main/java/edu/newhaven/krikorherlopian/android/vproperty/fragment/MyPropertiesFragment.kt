package edu.newhaven.krikorherlopian.android.vproperty.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.adapter.RecylerViewAdapter
import edu.newhaven.krikorherlopian.android.vproperty.callback.SwipeToDeleteCallback
import edu.newhaven.krikorherlopian.android.vproperty.fragmentActivityCommunication
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.ListClick
import edu.newhaven.krikorherlopian.android.vproperty.loggedInUser
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import kotlinx.android.synthetic.main.listview_fragment.*
import kotlinx.android.synthetic.main.listview_fragment.view.*

class MyPropertiesFragment : Fragment(), ListClick {
    var root: View? = null
    lateinit var adapter: RecylerViewAdapter
    var list: MutableList<Any> = mutableListOf<Any>()
    override fun rowClicked(position: Int, position2: Int, imageLayout: ImageView?) {
        fragmentActivityCommunication?.startActivityDetWithTransition(
            (list.get(position) as Property),
            imageLayout!!
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.listview_fragment, container, false)
        fragmentActivityCommunication!!.hideShowMenuItems(true)
        getData()
        return root
    }

    private fun getData() {
        val db = FirebaseFirestore.getInstance()
        var count = 0
        list.clear()
        root?.text?.visibility = View.VISIBLE
        adapter = RecylerViewAdapter(
            list, this, root?.context!!
        )
        root?.recyclerView?.layoutManager = LinearLayoutManager(root?.context)
        root?.recyclerView?.itemAnimator = DefaultItemAnimator()
        root?.recyclerView?.adapter = adapter

        val swipeHandler = object : SwipeToDeleteCallback(context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteRow(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(root?.recyclerView!!)

        val docRef = db.collection("properties")
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
                        if (inList == false && prop.email.equals(loggedInUser?.email) && prop.isDisabled.trim().equals(
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
                        if (prop.email.equals(loggedInUser?.email) && prop.isDisabled.trim().equals(
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

    override fun deleteRow(position: Int) {
        val db = FirebaseFirestore.getInstance()
        var prop = list.get(position) as Property
        prop.isDisabled = "Y"
        db.collection("properties")
            .document(prop.id)
            .set(prop)
            .addOnSuccessListener { documentReference ->
            }
            .addOnFailureListener { e ->
            }
        adapter.removeAt(position)
        val mySnackbar = Snackbar.make(
            coordinatorLayout,
            R.string.success_archived, Snackbar.LENGTH_SHORT
        )
        mySnackbar.setAction(R.string.undo, View.OnClickListener {
            returnRow(prop, position)
        })
        mySnackbar.show()
    }

    fun returnRow(prop: Property, position: Int) {
        val db = FirebaseFirestore.getInstance()
        prop.isDisabled = "N"
        db.collection("properties")
            .document(prop.id)
            .set(prop)
            .addOnSuccessListener { documentReference ->
            }
            .addOnFailureListener { e ->
            }
        adapter.add(position, prop)
    }


}

