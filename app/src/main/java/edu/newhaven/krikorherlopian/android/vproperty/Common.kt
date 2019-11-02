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
