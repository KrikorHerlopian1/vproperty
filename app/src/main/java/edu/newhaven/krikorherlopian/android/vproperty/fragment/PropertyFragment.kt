package edu.newhaven.krikorherlopian.android.vproperty.fragment


import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
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
import edu.newhaven.krikorherlopian.android.vproperty.*
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import kotlinx.android.synthetic.main.map_info.view.*

class PropertyFragment : Fragment(), OnMapReadyCallback {
    var propertyList = ArrayList<Property>()
    private var mLocationRequest: LocationRequest? = null
    private val SPEED_INTERVAL: Long = 20000 /* 20 sec */
    private val UPDATE_INTERVAL = (10 * 1000).toLong()  /* 10 secs */
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    lateinit var markerS: Marker
    var count = 0
    var prevLocation: Location? = null
    var currentView: ImageView? = null
    var sharedPref: SharedPreferences? = null
    lateinit var mapType: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.maps, container, false)
        fragmentActivityCommunication!!.hideShowMenuItems(true)
        sharedPref = root?.context?.getSharedPreferences(
            PREFS_FILENAME,
            PRIVATE_MODE
        )
        setUpPermissions()
        mapType = sharedPref?.getString(PREF_MAP, "normal").toString()
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(root.context)
        getData()
        getLocation()
        return root
    }

    fun setUpPermissions() {
        val permissions = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_NETWORK_STATE
        )
        requestPermissions(permissions, 0)
    }

    private fun getLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    val latLng = LatLng(
                        location.latitude.toDouble(),
                        location.longitude.toDouble()
                    )
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
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
                    DocumentChange.Type.MODIFIED -> {
                        var prop: Property = doc.document.toObject(Property::class.java)
                        var i = 0
                        var inList: Boolean = false
                        for (propert in propertyList) {
                            if (doc.document.id.equals(propert.id)) {
                                inList = true
                                propertyList[i] = prop
                                propertyList[i].id = doc.document.id
                                try {
                                    if (markerS.isInfoWindowShown) {
                                        markerS.showInfoWindow()
                                    }
                                } catch (e: java.lang.Exception) {
                                    Log.d("property frag", "Exception")
                                }
                            }
                            i = i + 1
                        }
                        if (inList == false && prop.isDisabled.trim().equals(
                                "N"
                            )
                        ) {
                            prop.id = doc.document.id
                            addPropertyToMap(prop, doc)
                        }
                    }
                    DocumentChange.Type.ADDED -> {
                        Log.d("TAGs", "${doc.document.id} => ${doc.document.data}")
                        try {
                            var property: Property = doc.document.toObject(Property::class.java)
                            if (property.isDisabled.trim().equals(
                                    "N"
                                )
                            ) {
                                addPropertyToMap(property, doc)
                            }
                        } catch (e: java.lang.Exception) {
                            Log.d("property frag", "Exception")
                        }

                    }
                }
            }
        }


    }

    private fun addPropertyToMap(propertyAdd: Property, doc: DocumentChange) {
        propertyAdd.id = doc.document.id
        propertyList.add(propertyAdd)
        try {
            val latLng =
                LatLng(
                    propertyAdd.address.latitude.toDouble(),
                    propertyAdd.address.longitude.toDouble()
                )

            mMap.addMarker(
                MarkerOptions().icon(getMarkerIcon()).position(latLng).title(
                    "" + (count)
                )
            )
            count = count + 1
            mMap.setOnInfoWindowClickListener {
                var property1: Property = propertyList.get(it.title.toInt())
                if (property1.isDisabled.trim().equals("N")) {
                    fragmentActivityCommunication?.startActivityDet(
                        property1
                    )
                }
            }
            mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                override fun getInfoWindow(arg0: Marker): View? {
                    return null
                }

                override fun getInfoContents(marker: Marker?): View {
                    var v: View? = null

                    var property: Property = propertyList.get(marker?.title?.toInt()!!)
                    if (property.isDisabled.trim().equals("Y")) {
                        v = layoutInflater.inflate(
                            R.layout.no_more_available, null
                        )
                        val displayMetrics = resources.displayMetrics
                        v.layoutParams = LinearLayout.LayoutParams(
                            displayMetrics.widthPixels,
                            resources.getDimension(R.dimen.image_map_size).toInt()
                        )
                    } else {
                        v = layoutInflater.inflate(
                            R.layout.map_info, null
                        )
                        markerS = marker
                        currentView = v.image
                        val displayMetrics = resources.displayMetrics
                        v.layoutParams = LinearLayout.LayoutParams(
                            displayMetrics.widthPixels,
                            resources.getDimension(R.dimen.image_map_size).toInt()
                        )
                        val i = Integer.parseInt(marker.title)
                        if (i != -1) {
                            val property = propertyList.get(i)
                            val info = v.info
                            info.text = "\u200e" + property.address.addressName
                            if (property.homeFacts.isSale) {
                                Glide.with(v.context).load(R.drawable.ic_for_sale).placeholder(
                                    R.drawable.ic_for_sale
                                ).into(
                                    v.imagetype
                                )
                            } else {
                                Glide.with(v.context).load(R.drawable.ic_for_rent_color)
                                    .placeholder(
                                        R.drawable.ic_for_rent_color
                                    ).into(
                                    v.imagetype
                                )
                            }

                            var priceHome = String.format(
                                "%,.2f",
                                property.homeFacts.price?.toFloat()
                            )
                            if (property.homeFacts.isRent) {
                                v.price.text = "\u200e" + priceHome + " $$"
                            } else
                                v.price.text = "\u200e" + priceHome + " $$"
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
                                            Log.d("property frag", "Exception")
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
                    return v!!
                }
            })

        } catch (e: java.lang.Exception) {
            Log.d("property frag", "Exception")
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mMap.isMyLocationEnabled = true
            }
        } else {
            mMap.isMyLocationEnabled = true
        }
        when (mapType) {
            "normal" -> mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            "hybrid" -> mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            "terrain" -> mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            "satellite" -> mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }
    }

    override fun onStart() {
        super.onStart()
        startLocationUpdates()
    }

    protected fun startLocationUpdates() {
        // initialize location request object
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = UPDATE_INTERVAL
            setFastestInterval(SPEED_INTERVAL)
        }

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(activity!!)
        settingsClient!!.checkLocationSettings(locationSettingsRequest)

        registerLocationListner()
    }

    private fun registerLocationListner() {
        // initialize location callback object
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                onLocationChanged(locationResult!!.lastLocation)
            }
        }
        if (Build.VERSION.SDK_INT >= 23 && checkPermission()) {
            LocationServices.getFusedLocationProviderClient(activity!!)
                .requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper())
        }
    }

    private fun onLocationChanged(location: Location) {
        try {
            if (prevLocation != null) {
                var R = 3958.756 // miles
                var φ1 = Math.toRadians(location.latitude)
                var φ2 = Math.toRadians(prevLocation?.latitude!!)
                var Δφ = Math.toRadians(prevLocation?.latitude!! - location.latitude)
                var Δλ = Math.toRadians(prevLocation?.longitude!! - location.longitude)

                var a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
                        Math.cos(φ1) * Math.cos(φ2) *
                        Math.sin(Δλ / 2) * Math.sin(Δλ / 2)
                var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
                var d = R * c
                if (d > 4) {
                    prevLocation = location
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLng(
                            LatLng(
                                location.latitude,
                                location.longitude
                            )
                        )
                    )
                }
            } else {
                mMap.moveCamera(
                    CameraUpdateFactory.newLatLng(
                        LatLng(
                            location.latitude,
                            location.longitude
                        )
                    )
                )
                prevLocation = location
            }

        } catch (e: java.lang.Exception) {
            Log.d("property frag", "Exception")
        }
    }

    private fun checkPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                activity!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            setUpPermissions()
            return false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            if (requestCode == 0) {
                if (permissions[0] == android.Manifest.permission.ACCESS_FINE_LOCATION) {
                    getLocation()
                    registerLocationListner()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(
                                context!!,
                                android.Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            mMap.isMyLocationEnabled = true
                        }
                    } else {
                        mMap.isMyLocationEnabled = true
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            Log.d("property frag", "Exception")
        }

    }

}