package edu.newhaven.krikorherlopian.android.vproperty.fragment


import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import kotlinx.android.synthetic.main.map_info.view.*

class PropertyFragment : Fragment(), OnMapReadyCallback {
    var propertyList = ArrayList<Property>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.maps, container, false)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(root.context)
        getData()
        getLocation()
        return root
    }

    private fun getLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    val sydney = LatLng(
                        location.latitude.toDouble(),
                        location.longitude.toDouble()
                    )
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10f))
                }
            }
    }

    private fun getData() {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("properties")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            for (doc in snapshot!!.documentChanges) {
                when (doc.type) {
                    DocumentChange.Type.ADDED -> {
                        Log.d("", "${doc.document.id} => ${doc.document.data}")
                        var property: Property = Property(
                            doc.document.data.get("houseName").toString(),
                            doc.document.data.get("addressName").toString(),
                            doc.document.data.get("zipCode").toString(),
                            doc.document.data.get("longitude").toString(),
                            doc.document.data.get("latitude").toString(),
                            doc.document.data.get("description").toString(),
                            doc.document.data.get("photoUrl").toString(),
                            doc.document.data.get("email").toString()
                        )
                        propertyList.add(property)
                        val sydney =
                            LatLng(property.latitude.toDouble(), property.longitude.toDouble())
                        mMap.addMarker(MarkerOptions().position(sydney).title("" + (propertyList.size - 1)))
                        mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                            override fun getInfoWindow(arg0: Marker): View? {
                                return null
                            }

                            override fun getInfoContents(marker: Marker?): View {
                                val v = layoutInflater.inflate(
                                    R.layout.map_info, null
                                )
                                val displayMetrics = resources.displayMetrics
                                v.layoutParams = LinearLayout.LayoutParams(
                                    displayMetrics.widthPixels - 100,
                                    resources.getDimension(R.dimen.image_map_size).toInt()
                                )
                                val i = Integer.parseInt(marker!!.title)
                                if (i != -1) {
                                    val property = propertyList.get(i)
                                    val info = v.info
                                    info.text = "\u200e" + property.addressName
                                    Picasso.get()
                                        .load(propertyList.get(i).photoUrl)
                                        .placeholder(R.drawable.placeholderdetail)
                                        .into(v.image, object : Callback {
                                            override fun onError(e: java.lang.Exception?) {
                                            }

                                            override fun onSuccess() {
                                                try {
                                                    if (marker != null && marker.isInfoWindowShown) {
                                                        marker.hideInfoWindow()
                                                        Picasso.get()
                                                            .load(propertyList.get(i).photoUrl)
                                                            .placeholder(R.drawable.placeholderdetail)
                                                            .into(v.image)
                                                        marker.showInfoWindow()
                                                    }
                                                } catch (e: Exception) {
                                                }

                                            }

                                            fun onError() {

                                            }
                                        })
                                    v.requestLayout()
                                } else {
                                }
                                return v
                            }
                        })

                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }
}