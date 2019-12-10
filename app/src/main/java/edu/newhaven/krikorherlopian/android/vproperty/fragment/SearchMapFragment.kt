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

class SearchMapFragment : Fragment(), OnMapReadyCallback {
    var propertyList = ArrayList<Property>()
    lateinit var prop: Property
    var min: String = ""
    var max: String = ""
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
        sharedPref = root?.context?.getSharedPreferences(
            PREFS_FILENAME,
            PRIVATE_MODE
        )
        prop = arguments?.getSerializable(SearchMapFragment.ARG_PARAM) as Property
        min = arguments?.getString(SearchMapFragment.ARG_MIN)!!
        max = arguments?.getString(SearchMapFragment.ARG_MAX)!!
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
                    if (prop.address.longitude.equals("")) {
                        val latLng = LatLng(
                            location.latitude.toDouble(),
                            location.longitude.toDouble()
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                    } else {
                        val latLng = LatLng(
                            prop.address.latitude.toDouble(),
                            prop.address.longitude.toDouble()
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                    }

                }
            }
    }

    private fun getData() {
        val db = FirebaseFirestore.getInstance()
        var docRef = db.collection("properties").whereGreaterThan("homeFacts.price", 0)
        if (prop.homeFacts.isRent) {
            docRef = db.collection("properties").whereEqualTo("homeFacts.rent", true)
        } else if (prop.homeFacts.isSale) {
            docRef = db.collection("properties").whereEqualTo("homeFacts.sale", true)
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
                                }
                            }
                            i = i + 1
                        }
                        if (inList == false && prop.isDisabled.trim().equals(
                                "N"
                            )
                        ) {
                            prop.id = doc.document.id
                            addPropertyCheck(prop, doc)
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
                                addPropertyCheck(property, doc)
                            }
                        } catch (e: java.lang.Exception) {
                        }

                    }
                }
            }
        }
    }

    private fun addPropertyCheck(pro: Property, doc: DocumentChange) {
        if ((prop.homeFacts.homeType.equals("") || prop.homeFacts.homeType.equals(pro.homeFacts.homeType))
            && (min.trim().equals("") || pro.homeFacts.price!! >= min.toFloat())
            && (max.trim().equals("") || pro.homeFacts.price!! <= max.toFloat())
            && (prop.roomDetails.appliances.washer.equals(false) || pro.roomDetails.appliances.washer.equals(
                true
            ))
            && (prop.roomDetails.appliances.trashCompactor.equals(false) || pro.roomDetails.appliances.trashCompactor.equals(
                true
            ))
            && (prop.roomDetails.appliances.refrigerator.equals(false) || pro.roomDetails.appliances.refrigerator.equals(
                true
            ))
            && (prop.roomDetails.appliances.rangeoven.equals(false) || pro.roomDetails.appliances.rangeoven.equals(
                true
            ))
            && (prop.roomDetails.appliances.microwave.equals(false) || pro.roomDetails.appliances.microwave.equals(
                true
            ))
            && (prop.roomDetails.appliances.disposal.equals(false) || pro.roomDetails.appliances.disposal.equals(
                true
            ))
            && (prop.roomDetails.appliances.freezer.equals(false) || pro.roomDetails.appliances.freezer.equals(
                true
            ))
            && (prop.roomDetails.appliances.dryer.equals(false) || pro.roomDetails.appliances.dryer.equals(
                true
            ))
            && (prop.roomDetails.appliances.dishWasher.equals(false) || pro.roomDetails.appliances.dishWasher.equals(
                true
            ))
            && (prop.roomDetails.floorCovering.carpet.equals(false) || pro.roomDetails.floorCovering.carpet.equals(
                true
            ))
            && (prop.roomDetails.floorCovering.hardwood.equals(false) || pro.roomDetails.floorCovering.hardwood.equals(
                true
            ))
            && (prop.roomDetails.floorCovering.concrete.equals(false) || pro.roomDetails.floorCovering.concrete.equals(
                true
            ))
            && (prop.roomDetails.floorCovering.laminate.equals(false) || pro.roomDetails.floorCovering.laminate.equals(
                true
            ))
            && (prop.roomDetails.floorCovering.slate.equals(false) || pro.roomDetails.floorCovering.slate.equals(
                true
            ))
            && (prop.roomDetails.floorCovering.softwood.equals(false) || pro.roomDetails.floorCovering.softwood.equals(
                true
            ))
            && (prop.roomDetails.floorCovering.linoleum.equals(false) || pro.roomDetails.floorCovering.linoleum.equals(
                true
            ))
            && (prop.roomDetails.floorCovering.tile.equals(false) || pro.roomDetails.floorCovering.tile.equals(
                true
            ))
            && (prop.roomDetails.floorCovering.other.equals(false) || pro.roomDetails.floorCovering.other.equals(
                true
            ))
            && (prop.roomDetails.rooms.breakfast.equals(false) || pro.roomDetails.rooms.breakfast.equals(
                true
            ))
            && (prop.roomDetails.rooms.dinning.equals(false) || pro.roomDetails.rooms.dinning.equals(
                true
            ))
            && (prop.roomDetails.rooms.family.equals(false) || pro.roomDetails.rooms.family.equals(
                true
            ))
            && (prop.roomDetails.rooms.laundry.equals(false) || pro.roomDetails.rooms.laundry.equals(
                true
            ))
            && (prop.roomDetails.rooms.library.equals(false) || pro.roomDetails.rooms.library.equals(
                true
            ))
            && (prop.roomDetails.rooms.masterBath.equals(false) || pro.roomDetails.rooms.masterBath.equals(
                true
            ))
            && (prop.roomDetails.rooms.mud.equals(false) || pro.roomDetails.rooms.mud.equals(
                true
            ))
            && (prop.roomDetails.rooms.office.equals(false) || pro.roomDetails.rooms.office.equals(
                true
            ))
            && (prop.roomDetails.rooms.pantry.equals(false) || pro.roomDetails.rooms.pantry.equals(
                true
            ))
            && (prop.roomDetails.rooms.recreation.equals(false) || pro.roomDetails.rooms.recreation.equals(
                true
            ))
            && (prop.roomDetails.rooms.workshop.equals(false) || pro.roomDetails.rooms.workshop.equals(
                true
            ))
            && (prop.roomDetails.rooms.solarium.equals(false) || pro.roomDetails.rooms.solarium.equals(
                true
            ))
            && (prop.roomDetails.rooms.sun.equals(false) || pro.roomDetails.rooms.sun.equals(
                true
            ))
            && (prop.roomDetails.rooms.walkInCloset.equals(false) || pro.roomDetails.rooms.walkInCloset.equals(
                true
            ))
            && (prop.buildingDetails.exterior.brick.equals(false) || pro.buildingDetails.exterior.brick.equals(
                true
            ))
            && (prop.buildingDetails.exterior.cement.equals(false) || pro.buildingDetails.exterior.cement.equals(
                true
            ))
            && (prop.buildingDetails.exterior.composition.equals(false) || pro.buildingDetails.exterior.composition.equals(
                true
            ))
            && (prop.buildingDetails.exterior.metal.equals(false) || pro.buildingDetails.exterior.metal.equals(
                true
            ))
            && (prop.buildingDetails.exterior.shingle.equals(false) || pro.buildingDetails.exterior.shingle.equals(
                true
            ))
            && (prop.buildingDetails.exterior.stone.equals(false) || pro.buildingDetails.exterior.stone.equals(
                true
            ))
            && (prop.buildingDetails.exterior.stucco.equals(false) || pro.buildingDetails.exterior.stucco.equals(
                true
            ))
            && (prop.buildingDetails.exterior.vinyl.equals(false) || pro.buildingDetails.exterior.vinyl.equals(
                true
            ))
            && (prop.buildingDetails.exterior.wood.equals(false) || pro.buildingDetails.exterior.wood.equals(
                true
            ))
            && (prop.buildingDetails.exterior.woodProducts.equals(false) || pro.buildingDetails.exterior.woodProducts.equals(
                true
            ))
            && (prop.buildingDetails.exterior.other.equals(false) || pro.buildingDetails.exterior.other.equals(
                true
            ))
            && (prop.buildingDetails.buildingAminities.basketballCourt.equals(false) || pro.buildingDetails.buildingAminities.basketballCourt.equals(
                true
            ))
            && (prop.buildingDetails.buildingAminities.disabledAccess.equals(false) || pro.buildingDetails.buildingAminities.disabledAccess.equals(
                true
            ))
            && (prop.buildingDetails.buildingAminities.doorman.equals(false) || pro.buildingDetails.buildingAminities.doorman.equals(
                true
            ))
            && (prop.buildingDetails.buildingAminities.elevator.equals(false) || pro.buildingDetails.buildingAminities.elevator.equals(
                true
            ))
            && (prop.buildingDetails.buildingAminities.fitnessCenter.equals(false) || pro.buildingDetails.buildingAminities.fitnessCenter.equals(
                true
            ))
            && (prop.buildingDetails.buildingAminities.sportsCourt.equals(false) || pro.buildingDetails.buildingAminities.sportsCourt.equals(
                true
            ))
            && (prop.buildingDetails.buildingAminities.storage.equals(false) || pro.buildingDetails.buildingAminities.storage.equals(
                true
            ))
            && (prop.buildingDetails.buildingAminities.tennisCourt.equals(false) || pro.buildingDetails.buildingAminities.tennisCourt.equals(
                true
            ))
            && (prop.buildingDetails.buildingAminities.nearTransportation.equals(false) || pro.buildingDetails.buildingAminities.nearTransportation.equals(
                true
            ))
            && (prop.buildingDetails.outdoorAminities.balconyPatio.equals(false) || pro.buildingDetails.outdoorAminities.balconyPatio.equals(
                true
            ))
            && (prop.buildingDetails.outdoorAminities.lawn.equals(false) || pro.buildingDetails.outdoorAminities.lawn.equals(
                true
            ))
            && (prop.buildingDetails.outdoorAminities.barbecueArea.equals(false) || pro.buildingDetails.outdoorAminities.barbecueArea.equals(
                true
            ))
            && (prop.buildingDetails.outdoorAminities.pond.equals(false) || pro.buildingDetails.outdoorAminities.pond.equals(
                true
            ))
            && (prop.buildingDetails.outdoorAminities.deck.equals(false) || pro.buildingDetails.outdoorAminities.deck.equals(
                true
            ))
            && (prop.buildingDetails.outdoorAminities.pool.equals(false) || pro.buildingDetails.outdoorAminities.pool.equals(
                true
            ))
            && (prop.buildingDetails.outdoorAminities.dock.equals(false) || pro.buildingDetails.outdoorAminities.dock.equals(
                true
            ))
            && (prop.buildingDetails.outdoorAminities.porch.equals(false) || pro.buildingDetails.outdoorAminities.porch.equals(
                true
            ))
            && (prop.buildingDetails.outdoorAminities.fencedYard.equals(false) || pro.buildingDetails.outdoorAminities.fencedYard.equals(
                true
            ))
            && (prop.buildingDetails.outdoorAminities.rvParking.equals(false) || pro.buildingDetails.outdoorAminities.rvParking.equals(
                true
            ))
            && (prop.buildingDetails.outdoorAminities.garden.equals(false) || pro.buildingDetails.outdoorAminities.garden.equals(
                true
            ))
            && (prop.buildingDetails.outdoorAminities.sauna.equals(false) || pro.buildingDetails.outdoorAminities.sauna.equals(
                true
            ))
            && (prop.buildingDetails.outdoorAminities.greenHouse.equals(false) || pro.buildingDetails.outdoorAminities.greenHouse.equals(
                true
            ))
            && (prop.buildingDetails.outdoorAminities.sprinkerlSystem.equals(false) || pro.buildingDetails.outdoorAminities.sprinkerlSystem.equals(
                true
            ))
            && (prop.buildingDetails.outdoorAminities.hotTubSpa.equals(false) || pro.buildingDetails.outdoorAminities.hotTubSpa.equals(
                true
            ))
            && (prop.buildingDetails.outdoorAminities.waterfront.equals(false) || pro.buildingDetails.outdoorAminities.waterfront.equals(
                true
            ))
            && (prop.buildingDetails.parking.carport.equals(false) || pro.buildingDetails.parking.carport.equals(
                true
            ))
            && (prop.buildingDetails.parking.onStreet.equals(false) || pro.buildingDetails.parking.onStreet.equals(
                true
            ))
            && (prop.buildingDetails.parking.offStreet.equals(false) || pro.buildingDetails.parking.offStreet.equals(
                true
            ))
            && (prop.buildingDetails.parking.garageAttached.equals(false) || pro.buildingDetails.parking.garageAttached.equals(
                true
            ))
            && (prop.buildingDetails.parking.garageDetached.equals(false) || pro.buildingDetails.parking.garageDetached.equals(
                true
            ))
            && (prop.buildingDetails.parking.none.equals(false) || pro.buildingDetails.parking.none.equals(
                true
            ))
            && (prop.utilityDetails.heatingType.baseboard.equals(false) || pro.utilityDetails.heatingType.baseboard.equals(
                true
            ))
            && (prop.utilityDetails.heatingType.forcedAir.equals(false) || pro.utilityDetails.heatingType.forcedAir.equals(
                true
            ))
            && (prop.utilityDetails.heatingType.geoThermal.equals(false) || pro.utilityDetails.heatingType.geoThermal.equals(
                true
            ))
            && (prop.utilityDetails.heatingType.heatPump.equals(false) || pro.utilityDetails.heatingType.heatPump.equals(
                true
            ))
            && (prop.utilityDetails.heatingType.radiant.equals(false) || pro.utilityDetails.heatingType.radiant.equals(
                true
            ))
            && (prop.utilityDetails.heatingType.stove.equals(false) || pro.utilityDetails.heatingType.stove.equals(
                true
            ))
            && (prop.utilityDetails.heatingType.wall.equals(false) || pro.utilityDetails.heatingType.wall.equals(
                true
            ))
            && (prop.utilityDetails.heatingType.other.equals(false) || pro.utilityDetails.heatingType.other.equals(
                true
            ))
            && (prop.utilityDetails.coolingType.central.equals(false) || pro.utilityDetails.coolingType.central.equals(
                true
            ))
            && (prop.utilityDetails.coolingType.evaporative.equals(false) || pro.utilityDetails.coolingType.evaporative.equals(
                true
            ))
            && (prop.utilityDetails.coolingType.geoThermal.equals(false) || pro.utilityDetails.coolingType.geoThermal.equals(
                true
            ))
            && (prop.utilityDetails.coolingType.refrigeration.equals(false) || pro.utilityDetails.coolingType.refrigeration.equals(
                true
            ))
            && (prop.utilityDetails.coolingType.solar.equals(false) || pro.utilityDetails.coolingType.solar.equals(
                true
            ))
            && (prop.utilityDetails.coolingType.wall.equals(false) || pro.utilityDetails.coolingType.wall.equals(
                true
            ))
            && (prop.utilityDetails.coolingType.other.equals(false) || pro.utilityDetails.coolingType.other.equals(
                true
            ))
            && (prop.utilityDetails.coolingType.none.equals(false) || pro.utilityDetails.coolingType.none.equals(
                true
            ))
        ) {
            addPropertyToMap(pro, doc)
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


            /* mMap.moveCamera(
                 CameraUpdateFactory.newLatLng(
                     LatLng(
                         location.latitude,
                         location.longitude
                     )
                 )
             )*/
        } catch (e: java.lang.Exception) {
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
        }

    }


    companion object {
        private const val ARG_PARAM = "param"
        private const val ARG_MIN = "0"
        private const val ARG_MAX = "1"
        @JvmStatic
        fun newInstance(
            sectionNumber: Int,
            param: Property,
            min: String,
            max: String
        ): SearchMapFragment {
            return SearchMapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MAX, max)
                    putString(ARG_MIN, min)
                    putSerializable(ARG_PARAM, param)
                }
            }
        }
    }

}