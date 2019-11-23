package edu.newhaven.krikorherlopian.android.vproperty

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.ActivityCompat
import com.anupcowkur.statelin.State
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.auth.FirebaseUser
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.ActivityFunctionalities
import edu.newhaven.krikorherlopian.android.vproperty.interfaces.FragmentActivityCommunication
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import java.util.*

/*
    Common functions I will need accross various activities or fragments in application.
 */
var PRIVATE_MODE = 0
val PREFS_FILENAME = "vpropertyapp"
val PREF_PASS = "password"
val PREF_EMAIL = "email"
val PREF_TOKEN = "token"
val PREF_DRAWER = "drawer"
val PREF_MAP = "maptype"
val PREF_AUTO = "autologin"
val PREF_NOT = "notifications"
val PREF_LOCALE = "lang"
var loggedInUser: FirebaseUser? = null
var font = "Poppins-Light.ttf"
var activityFunctionalities: ActivityFunctionalities? = null
var fragmentActivityCommunication: FragmentActivityCommunication? = null
val stateView = State("view")
val stateCreate = State("create")
val stateUpdate = State("update")
val addModifyProperty = State("addModifyProperty")
val internetCall = State("internetCall")
val color: String = "#673AB7"
fun getMarkerIcon(): BitmapDescriptor {
    val hsv = FloatArray(3)
    Color.colorToHSV(Color.parseColor(color), hsv)
    return BitmapDescriptorFactory.defaultMarker(hsv[0])
}

fun isEmailValid(email: CharSequence): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isPermissionGranted(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.USE_BIOMETRIC
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.USE_FINGERPRINT
        ) == PackageManager.PERMISSION_GRANTED
    }
}

fun setUpPermissions(activity: Activity) {
    val permissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.ACCESS_NETWORK_STATE
    )
    ActivityCompat.requestPermissions(activity, permissions, 0)
}

fun sortByDistance(list: MutableList<Any>, longitude: String, latitude: String): MutableList<Any> {
    for (pro in list) {
        var R = 3958.756 // miles
        var φ1 = Math.toRadians(latitude.toDouble())
        var φ2 =
            Math.toRadians((pro as Property).address.latitude.toDouble())
        var Δφ =
            Math.toRadians(pro.address.latitude.toDouble() - latitude.toDouble())
        var Δλ =
            Math.toRadians(pro.address.longitude.toDouble() - longitude.toDouble())

        var a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
                Math.cos(φ1) * Math.cos(φ2) *
                Math.sin(Δλ / 2) * Math.sin(Δλ / 2)
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        var d = R * c
        pro.distance = d
    }
    val customComparator = object : Comparator<Property> {
        override fun compare(a: Property, b: Property): Int {
            if ((a.distance - b.distance) > 0.0)
                return 1
            else
                return -1
        }
    }
    Collections.sort(list as MutableList<Property>, customComparator)
    return list
}
