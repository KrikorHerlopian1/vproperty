package edu.newhaven.krikorherlopian.android.vproperty.fragment

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.model.Property

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
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 4f))
                }
            }
    }

    private fun getData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("properties")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("", "${document.id} => ${document.data}")
                    var property: Property = Property(
                        document.data.get("houseName").toString(),
                        document.data.get("addressName").toString(),
                        document.data.get("zipCode").toString(),
                        document.data.get("longitude").toString(),
                        document.data.get("latitude").toString(),
                        document.data.get("description").toString(),
                        document.data.get("photoUrl").toString(),
                        document.data.get("email").toString()
                    )
                    propertyList.add(property)
                    val sydney = LatLng(property.latitude.toDouble(), property.longitude.toDouble())
                    mMap.addMarker(MarkerOptions().position(sydney).title("" + property.houseName))
                }
            }
            .addOnFailureListener { exception ->
                Log.w("", "Error getting documents.", exception)
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        //val sydney = LatLng(-34.0, 151.0)
        //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}