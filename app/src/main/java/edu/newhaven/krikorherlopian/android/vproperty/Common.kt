package edu.newhaven.krikorherlopian.android.vproperty

var PRIVATE_MODE = 0
val PREFS_FILENAME = "vpropertyapp"
val PREF_PASS = "password"
val PREF_EMAIL = "email"

fun isEmailValid(email: CharSequence): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}