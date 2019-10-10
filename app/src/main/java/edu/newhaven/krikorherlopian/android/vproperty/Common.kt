package edu.newhaven.krikorherlopian.android.vproperty

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseUser

/*
    Common functions I will need accross various activities or fragments in application.
 */
var PRIVATE_MODE = 0
val PREFS_FILENAME = "vpropertyapp"
val PREF_PASS = "password"
val PREF_EMAIL = "email"
val PREF_TOKEN = "token"
val PREF_DRAWER = "drawer"
var loggedInUser: FirebaseUser? = null
var font = "Poppins-Light.ttf"
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
