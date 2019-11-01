package edu.newhaven.krikorherlopian.android.vproperty.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.adapter.RecylerViewAdapter
import edu.newhaven.krikorherlopian.android.vproperty.fragmentActivityCommunication
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.ListClick
import edu.newhaven.krikorherlopian.android.vproperty.loggedInUser
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
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
        getData()
        return root
    }

    private fun getData() {
        val db = FirebaseFirestore.getInstance()
        var count = 0
        list.clear()
        adapter = RecylerViewAdapter(
            list, this, root?.context!!
        )
        root?.recyclerView?.layoutManager = LinearLayoutManager(root?.context)
        root?.recyclerView?.itemAnimator = DefaultItemAnimator()
        root?.recyclerView?.adapter = adapter
        /*val docRef = db.collection("properties").get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    for(prop in document.toList()){
                        var property = prop.toObject(Property::class.java)
                        if(property.email.equals(loggedInUser?.email)) {
                            list.add(property)
                        }
                    }
                    adapter.notifyDataSetChanged()
                } else {
                }
            }
            .addOnFailureListener { exception ->
            }*/
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
                        for (propert in list) {
                            if (doc.document.id.equals((propert as Property).id)) {
                                prop.id = doc.document.id
                                list[i] = prop
                            }
                            i = i + 1
                        }
                    }
                    DocumentChange.Type.ADDED -> {
                        var prop: Property = doc.document.toObject(Property::class.java)
                        if (prop.email.equals(loggedInUser?.email)) {
                            prop.id = doc.document.id
                            list.add(prop)
                        }

                    }
                }
            }
            adapter.notifyDataSetChanged()
        }
    }
}

